package com.example.pickingparking.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class FirebaseConfig {

    // 마스터키 파일 경로를 읽
    @Value("${firebase.key.path}")
    private String firebaseKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            // 마스터키 파일을 읽
            String serviceAccountPath = firebaseKeyPath.replace("file:", "");
            InputStream serviceAccount = new FileInputStream(serviceAccountPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Firebase 앱이 이미 초기화되었는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp 초기화 성공!");
            }
        } catch (Exception e) {
            System.err.println("!!!!!!!!!! FirebaseApp 초기화 실패 !!!!!!!!!!");
            e.printStackTrace();
            System.err.println("FirebaseApp 초기화 실패: " + e.getMessage());
        }
    }
}
