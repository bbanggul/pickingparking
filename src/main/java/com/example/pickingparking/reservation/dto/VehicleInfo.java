package com.example.pickingparking.reservation.dto;

import com.example.pickingparking.vehicle.Vehicle;
import lombok.Getter;


@Getter
public class VehicleInfo {
    private final String licensePlate;
    private final String carModel;

    public VehicleInfo(Vehicle vehicle) {
        this.licensePlate = vehicle.getLicensePlate();
        this.carModel = vehicle.getCarModel();
    }
}
