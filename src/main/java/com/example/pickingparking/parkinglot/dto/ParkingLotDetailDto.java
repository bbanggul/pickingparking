package com.example.pickingparking.parkinglot.dto;

import com.example.pickingparking.parkinglot.ParkingLot;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ParkingLotDetailDto {
    private final Integer lotId;
    private final String name;
    private final String address;
    private final Integer totalSpaces;
    private final Integer availableSpaces;
    private final List<ParkingSpaceDto> spaces;

    public ParkingLotDetailDto(ParkingLot parkingLot) {
        this.lotId = parkingLot.getLotId();
        this.name = parkingLot.getName();
        this.address = parkingLot.getAddress();
        this.totalSpaces = parkingLot.getTotalSpaces();
        this.availableSpaces = parkingLot.getAvailableSpaces();
        this.spaces = parkingLot.getSpaces().stream()
                .map(ParkingSpaceDto::new)
                .collect(Collectors.toList());
    }
}

