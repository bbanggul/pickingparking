package com.example.pickingparking.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            System.out.println("FCM 토큰이 없어서 알림을 보낼 수 없습니다.");
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("성공적으로 알림을 보냈습니다: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("알림 보내기 실패: " + e.getMessage());
        }
    }
}
