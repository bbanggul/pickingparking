package com.example.pickingparking.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity // db 연동
@Table(name = "Users") // db 유저랑연동
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id값 자동생성
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, name="phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "role")
    private String role;

}
