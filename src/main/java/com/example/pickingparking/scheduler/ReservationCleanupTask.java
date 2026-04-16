package com.example.pickingparking.scheduler;

import com.example.pickingparking.parkinglot.ParkingLot;
import com.example.pickingparking.parkinglot.ParkingLotRepository;
import com.example.pickingparking.parkinglot.ParkingSpace;
import com.example.pickingparking.parkinglot.ParkingSpaceRepository;
import com.example.pickingparking.reservation.Reservation;
import com.example.pickingparking.reservation.ReservationRepository;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationCleanupTask {

    private final ReservationRepository reservationRepository;
    @Getter
    private final ParkingSpaceRepository parkingSpaceRepository;
    @Getter
    private final ParkingLotRepository parkingLotRepository;

    public ReservationCleanupTask(ReservationRepository reservationRepository, ParkingSpaceRepository parkingSpaceRepository, ParkingLotRepository parkingLotRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    // cron = "0 0 * * * *"는 "매시간 0분 0초에 이 작업을 실행해라"
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredReservations() {
        System.out.println("\n--- [스케줄러] 만료된 예약 정리 작업 시작: " + LocalDateTime.now() + " ---");

        //  현재 시간 이전에 '종료'된 '활성' 상태의 예약을 모두 찾습니다.
        List<Reservation> expiredReservations = reservationRepository.findAllByEndTimeBeforeAndStatus(LocalDateTime.now(), "active");

        if (expiredReservations.isEmpty()) {
            System.out.println(" -> [스케줄러] 정리할 만료된 예약이 없습니다.");
            return;
        }

        System.out.println(" -> [스케줄러] " + expiredReservations.size() + "개의 만료된 예약을 찾았습니다.");

        for (Reservation reservation : expiredReservations) {
            //  'active'에서 'completed'로 변경
            //    (데이터를 바로 삭제하지 않는 이유: 나중에 '과거 이용내역' 같은 기능을 위해 기록을 남겨두는 것이 좋습니다.)
            reservation.setStatus("completed");

            //  예약되었던 주차 공간의 상태를 'reserved'에서 'available'로 다시 풀어줍니다.
            ParkingSpace parkingSpace = reservation.getParkingSpace();
            parkingSpace.setStatus("available");

            // 4. 주차장의 전체 이용 가능 공간 수를 다시 +1 해줍니다.
            ParkingLot parkingLot = parkingSpace.getParkingLot();
            parkingLot.setAvailableSpaces(parkingLot.getAvailableSpaces() + 1);

            // @Transactional 이 메소드가 끝나면 변경된 모든 내용이 DB에 자동으로 저장
        }

        System.out.println("--- [스케줄러] 만료된 예약 정리 작업 완료 ---\n");
    }

}
