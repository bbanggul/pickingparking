package com.example.pickingparking.parkinglot;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ParkingLots")
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_id")
    private Integer lotId;

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    @Column(name = "total_spaces")
    private Integer totalSpaces;
    @Column(name = "available_spaces")
    private Integer availableSpaces;
    @OneToMany(mappedBy = "parkingLot", fetch = FetchType.LAZY)
    private List<ParkingSpace> spaces;
}

