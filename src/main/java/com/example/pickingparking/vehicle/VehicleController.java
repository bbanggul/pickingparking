package com.example.pickingparking.vehicle;

import com.example.pickingparking.user.UserRepository;
import com.example.pickingparking.user.User;
import com.example.pickingparking.vehicle.dto.VehicleRegister;
import com.example.pickingparking.vehicle.dto.VehicleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Autowired
    public VehicleController(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> registerVehicle(@RequestBody VehicleRegister vehicleDto, Principal principal) {
        // ★★★ 블랙박스 시작 ★★★
        try {
            System.out.println("\n--- [CCTV] 차량 등록 API 시작 ---");
            System.out.println("[CCTV] 요청 수신 데이터: licensePlate=" + vehicleDto.getLicensePlate() + ", carModel=" + vehicleDto.getCarModel());

            // 1. 사용자 정보 조회
            System.out.println("[CCTV] 1. JWT 토큰에서 사용자 이메일 추출: " + principal.getName());
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
            System.out.println(" -> [CCTV] 사용자 정보 조회 성공: " + user.getEmail());

            // 2. 새로운 Vehicle 객체 생성
            System.out.println("[CCTV] 2. 새 차량 객체 생성 및 정보 설정...");
            Vehicle newVehicle = new Vehicle();
            newVehicle.setLicensePlate(vehicleDto.getLicensePlate());
            newVehicle.setCarModel(vehicleDto.getCarModel());
            newVehicle.setUser(user);
            System.out.println(" -> [CCTV] 차량 객체 설정 완료.");

            // 3. DB에 차량 정보 저장
            System.out.println("[CCTV] 3. DB에 차량 정보 저장 시도...");
            Vehicle savedVehicle = vehicleRepository.save(newVehicle);
            System.out.println(" -> [CCTV] 차량 정보 저장 성공! ID: " + savedVehicle.getVehicleId());

            // 4. DTO로 변환하여 응답
            VehicleResponse responseDto = new VehicleResponse(savedVehicle);
            System.out.println("--- [CCTV] 차량 등록 API 성공적으로 종료 ---\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (Exception e) {
            // ★★★ 만약 에러가 터지면, 여기서 잡아서 '진짜 범인'의 정체를 출력합니다 ★★★
            System.err.println("\n\n!!!!!!!!!! [블랙박스] 차량 등록 중 '진짜 에러' 발생 !!!!!!!!!!");
            e.printStackTrace(); // 에러의 모든 내용을 빨간 글씨로 출력
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");

            // Postman(클라이언트)에는 간단한 에러 메시지만 보냅니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류 발생! 콘솔 로그를 확인하세요: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getMyVehicles(Principal principal) {
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<Vehicle> vehicles = vehicleRepository.findByUser(user);

        List<VehicleResponse> responseDtos = vehicles.stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Integer vehicleId, Principal principal) {

        String userEmail = principal.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));


        Vehicle vehicleToDelete = vehicleRepository.findById(vehicleId) //삭제차량찾기
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "차량을 찾을 수 없습니다."));

        if (!vehicleToDelete.getUser().getUserId().equals(currentUser.getUserId())) { //차량주인과 삭제하려는사용자 일치 확인
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이 차량을 삭제할 권한이 없습니다."); //주인다르면 ㄴㄴ
        }
        vehicleRepository.delete(vehicleToDelete);

        return ResponseEntity.noContent().build();
    }
}

