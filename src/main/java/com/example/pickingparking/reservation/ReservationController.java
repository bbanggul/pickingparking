package com.example.pickingparking.reservation;

import com.example.pickingparking.parkinglot.ParkingLot;
import com.example.pickingparking.parkinglot.ParkingLotRepository;
import com.example.pickingparking.parkinglot.ParkingSpace;
import com.example.pickingparking.parkinglot.ParkingSpaceRepository;
import com.example.pickingparking.reservation.dto.ReservationRequestDto;
import com.example.pickingparking.reservation.dto.ReservationResponseDto;
import com.example.pickingparking.user.User;
import com.example.pickingparking.user.UserRepository;
import com.example.pickingparking.vehicle.Vehicle;
import com.example.pickingparking.vehicle.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final VehicleRepository vehicleRepository;

    public ReservationController(ReservationRepository reservationRepository, UserRepository userRepository, ParkingLotRepository parkingLotRepository, ParkingSpaceRepository parkingSpaceRepository, VehicleRepository vehicleRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDto reservationDto, Principal principal) {
        // ★★★ 블랙박스 시작 ★★★
        try {
            System.out.println("\n--- [CCTV] 예약 API 시작 ---");

            // 1. 사용자 정보 조회
            System.out.println("[CCTV] 1. JWT 토큰에서 사용자 이메일 추출: " + principal.getName());
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
            System.out.println(" -> [CCTV] 사용자 정보 조회 성공: " + user.getEmail());

            // 2. 차량 정보 조회
            System.out.println("[CCTV] 2. 차량 정보 조회 시도... (ID: " + reservationDto.getVehicleId() + ")");
            Vehicle vehicle = vehicleRepository.findById(reservationDto.getVehicleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "차량을 찾을 수 없습니다."));
            System.out.println(" -> [CCTV] 차량 정보 조회 성공: " + vehicle.getLicensePlate());

            // 3. 주차 공간 정보 조회
            System.out.println("[CCTV] 3. 주차 공간 정보 조회 시도... (ID: " + reservationDto.getSpaceId() + ")");
            ParkingSpace parkingSpace = parkingSpaceRepository.findById(reservationDto.getSpaceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주차 공간을 찾을 수 없습니다."));
            System.out.println(" -> [CCTV] 주차 공간 정보 조회 성공: " + parkingSpace.getSpaceNumber());

            // 4. 예약 가능 여부 확인
            if (!parkingSpace.getStatus().equals("available")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중이거나 예약된 공간입니다.");
            }
            Reservation newReservation = new Reservation();
            newReservation.setUser(user);
            newReservation.setParkingSpace(parkingSpace);
            newReservation.setVehicle(vehicle);
            newReservation.setStartTime(reservationDto.getStartTime());
            newReservation.setEndTime(reservationDto.getEndTime());
            newReservation.setStatus("active");
            reservationRepository.save(newReservation);

            parkingSpace.setStatus("reserved");
            parkingSpaceRepository.save(parkingSpace);

            ParkingLot parkingLot = parkingSpace.getParkingLot();
            parkingLot.setAvailableSpaces(parkingLot.getAvailableSpaces() - 1);
            parkingLotRepository.save(parkingLot);

            System.out.println("--- [CCTV] 예약 API 성공적으로 종료 ---\n");
            return ResponseEntity.status(HttpStatus.CREATED).body("좌석 지정 예약이 성공적으로 완료되었습니다.");

        } catch (Exception e) {
            // ★★★ 만약 에러가 터지면, 여기서 잡아서 '진짜 범인'의 정체를 출력합니다 ★★★
            System.err.println("\n\n!!!!!!!!!! [블랙박스] 예약 처리 중 '진짜 에러' 발생 !!!!!!!!!!");
            e.printStackTrace(); // 에러의 모든 내용을 빨간 글씨로 출력
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
            // Postman(클라이언트)에는 간단한 에러 메시지만 보냅니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류 발생! 콘솔 로그를 확인하세요.");
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponseDto>> getMyReservations(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findByUserOrderByStartTimeDesc(user);

        List<ReservationResponseDto> responseDtos = reservations.stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }


    @DeleteMapping("/{reservationId}")
    @Transactional
    public ResponseEntity<?> cancelReservation(@PathVariable Integer reservationId, Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."));

        if (!reservation.getUser().getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이 예약을 취소할 권한이 없습니다.");
        }

        ParkingSpace parkingSpace = reservation.getParkingSpace();
        parkingSpace.setStatus("available");
        parkingSpaceRepository.save(parkingSpace);

        ParkingLot parkingLot = parkingSpace.getParkingLot();
        parkingLot.setAvailableSpaces(parkingLot.getAvailableSpaces() + 1);
        parkingLotRepository.save(parkingLot);

        reservationRepository.delete(reservation);

        return ResponseEntity.noContent().build();
    }
}
