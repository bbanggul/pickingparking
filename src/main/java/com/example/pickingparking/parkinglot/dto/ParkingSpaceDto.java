package com.example.pickingparking.parkinglot.dto;

import com.example.pickingparking.parkinglot.ParkingSpace;
import lombok.Getter;

@Getter
public class ParkingSpaceDto {
    private final Integer spaceId;
    private final String spaceNumber;
    private final String status;

    public ParkingSpaceDto(ParkingSpace space) {
        this.spaceId = space.getSpaceId();
        this.spaceNumber = space.getSpaceNumber();
        this.status = space.getStatus();
    }
}

