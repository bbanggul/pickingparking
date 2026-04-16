package com.example.pickingparking.parkinglot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingLotDto {
    private Integer lotId;
    private String name;
    private String address;
    private Integer availableSpaces;
    private Double distance; // 사용자와의 거리를 담을 필드

    public ParkingLotDto(Integer lotId, String name, String address, Integer availableSpaces, Double distance) {
        this.lotId = lotId;
        this.name = name;
        this.address = address;
        this.availableSpaces = availableSpaces;
        this.distance = distance;
    }
}

