package com.example.pickingparking.parkinglog;

import com.example.pickingparking.fcm.FcmService;
import com.example.pickingparking.parkinglog.dto.CheckEntryRequestDto;
import com.example.pickingparking.reservation.Reservation;
import com.example.pickingparking.reservation.ReservationRepository;
import com.example.pickingparking.user.User;
import com.example.pickingparking.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List; // ★ List 임포트 추가
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-logs")
public class ParkingLogController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    public ParkingLogController(ReservationRepository reservationRepository, UserRepository userRepository, FcmService fcmService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    @PostMapping("/check-entry")
    public ResponseEntity<?> checkVehicleEntry(@RequestBody CheckEntryRequestDto checkEntryDto) {
        // ★★★ 블랙박스 시작 ★★★
        try {
            System.out.println("\n--- [CCTV] 하드웨어 API 요청 시작 ---");
            System.out.println("[CCTV] 요청 데이터: Space ID=" + checkEntryDto.getSpaceId() + ", 번호판=" + checkEntryDto.getLicensePlate());

            LocalDateTime now = LocalDateTime.now();
            System.out.println("[CCTV] 서버 현재 시간 (KST): " + now);

            // 1. DB에서 현재 시간, 주차 공간에 유효한 예약을 찾습니다.
            System.out.println("[CCTV] 1. 유효 예약 검색 중...");
            Optional<Reservation> activeReservationOpt = reservationRepository.findActiveReservationForSpace(
                    checkEntryDto.getSpaceId(), now
            );

            // 2. 유효한 예약이 있는지 확인
            if (activeReservationOpt.isPresent()) {
                Reservation activeReservation = activeReservationOpt.get();
                String reservedPlate = activeReservation.getVehicle().getLicensePlate();
                User user = activeReservation.getUser();
                System.out.println(" -> [CCTV] 유효한 예약 발견! 예약 차량 번호: " + reservedPlate);

                // 3. 번호판 일치 확인
                if (reservedPlate.equals(checkEntryDto.getLicensePlate())) {
                    // CASE 1: 번호판 일치 (정상 입차)
                    System.out.println(" -> [CCTV] 번호판 일치! 정상 입차 처리.");

                    // 알림 발송 로직
                    fcmService.sendNotification(
                            user.getFcmToken(),
                            "PickingParking 입차 확인",
                            "예약하신 차량(" + reservedPlate + ")이 정상적으로 입차되었습니다."
                    );
                    System.out.println(" -> [CCTV] 예약자 알림 발송 완료.");

                    return ResponseEntity.ok(Map.of("status", "ALLOWED", "message", "예약된 차량의 정상 입차입니다."));
                } else {
                    // CASE 2: 번호판 불일치 (불법 점유!) - 알림 발송
                    System.out.println(" -> [CCTV] 번호판 불일치! 다른 차량 침범 감지. 관리자 확인 필요");

                    // [★ 수정된 부분 ★] 관리자에게 알림 발송
                    sendNotificationToAdmins(
                            String.valueOf(checkEntryDto.getSpaceId()),
                            checkEntryDto.getLicensePlate(),
                            "예약 불일치" // 사유
                    );

                    return ResponseEntity.status(403).body(Map.of("status", "DENIED", "message", "해당 공간에 예약된 차량이 아닙니다."));
                }
            } else {
                // CASE 3: 유효한 예약 자체가 없는 공간에 주차 (무단 주차)
                System.out.println(" -> [CCTV] 유효한 예약 없음. 무단 주차로 간주.");
                return ResponseEntity.status(403).body(Map.of("status", "DENIED", "message", "예약되지 않은 공간입니다."));
            }

        } catch (Exception e) {
            // ★★★ 만약 에러가 터지면, 여기서 잡아서 '진짜 범인'의 정체를 출력합니다 ★★★
            System.err.println("\n\n!!!!!!!!!! [블랙박스] 하드웨어 API 처리 중 심각한 오류 발생 !!!!!!!!!!");
            e.printStackTrace(); // 에러의 모든 내용을 빨간 글씨로 출력
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
            // Postman(클라이언트)에는 간단한 에러 메시지만 보냅니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류 발생! 콘솔 로그를 확인하세요.");
        }
    }

    private void sendNotificationToAdmins(String spaceId, String detectedPlate, String reason) {
        System.out.println(" -> [CCTV] 관리자 알림 발송 시작. 사유: " + reason);
        try {
            List<User> admins = userRepository.findByRole("ADMIN");

            if (admins.isEmpty()) {
                System.out.println(" -> [CCTV] 알림을 받을 관리자 계정이 설정되어 있지 않습니다.");
                return;
            }

            String title = "PickingParking 비정상 입차 감지";
            String body = String.format("공간 [%s]에 비정상 입차가 감지되었습니다. (사유: %s, 감지번호: %s)",
                    spaceId, reason, detectedPlate);

            int successCount = 0;
            for (User admin : admins) {
                if (admin.getFcmToken() != null && !admin.getFcmToken().isEmpty()) {
                    fcmService.sendNotification(admin.getFcmToken(), title, body);
                    successCount++;
                }
            }
            System.out.println(" -> [CCTV] 총 " + successCount + "명의 관리자에게 알림 발송 완료.");

        } catch (Exception e) {
            System.err.println("[CCTV] 관리자 알림 발송 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 콘솔에 에러 로그 출력
        }
    }
}