package com.example.pickingparking.user;

import com.example.pickingparking.jwt.JwtUtil;
import com.example.pickingparking.user.dto.LoginRequest;
import com.example.pickingparking.user.dto.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.Principal;
import com.example.pickingparking.user.dto.FcmTokenRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody SignUpRequest signUpRequest) {
        User newUser = new User();
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword())); //비밀번호를 '암호 금고'에 넣어서 암호화한 뒤 저장
        newUser.setName(signUpRequest.getName());
        newUser.setPhoneNumber(signUpRequest.getPhoneNumber());
        newUser.setRole("ROLE_USER");
        userRepository.save(newUser);
        newUser.setCreatedAt(java.time.LocalDateTime.now().toString());
        return "회원가입 성공!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            //  암호 금고의 비교 기능을 사용해 비밀번호가 맞는지 확인
            // 사용자가 보낸 생 비밀번호, DB에 저장된 암호화된 비밀번호
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                final String jwt = jwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(Map.of("token", jwt));
            }
        }

        return ResponseEntity.status(401).body("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
    @GetMapping("/test")
    public String test() {
        return "인증된 사용자만 접근 가능한 페이지입니다!";
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<?> registerFcmToken(@RequestBody FcmTokenRequest tokenRequest, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        user.setFcmToken(tokenRequest.getFcmToken());
        userRepository.save(user);

        return ResponseEntity.ok("FCM 토큰이 성공적으로 등록되었습니다.");
    }
}