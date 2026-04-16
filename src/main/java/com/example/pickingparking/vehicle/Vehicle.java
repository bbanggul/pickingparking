package com.example.pickingparking.vehicle;

import com.example.pickingparking.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vehicle_id")
    private Integer vehicleId;

    @Column(nullable = false, unique = true,name="license_plate")
    private String licensePlate;

    private String carModel;

    // 차량(Many)은 한 명의 사용자(One)에게 속합니다.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


