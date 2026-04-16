package com.example.pickingparking.vehicle.dto;

import com.example.pickingparking.vehicle.Vehicle;
import lombok.Getter;

@Getter
public class VehicleResponse {
    private final Integer vehicleId;
    private final String licensePlate;
    private final String carModel;

    public VehicleResponse(Vehicle vehicle) {
        this.vehicleId = vehicle.getVehicleId();
        this.licensePlate = vehicle.getLicensePlate();
        this.carModel = vehicle.getCarModel();
    }
}
