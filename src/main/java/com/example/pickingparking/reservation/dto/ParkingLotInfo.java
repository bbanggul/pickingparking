package com.example.pickingparking.reservation.dto;

import com.example.pickingparking.parkinglot.ParkingLot;
import lombok.Getter;

@Getter
public class ParkingLotInfo {
    private final String name;
    private final String address;

    public ParkingLotInfo(ParkingLot parkingLot) {
        this.name = parkingLot.getName();
        this.address = parkingLot.getAddress();
    }
}
