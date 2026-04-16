package com.example.pickingparking.reservation.dto;

import com.example.pickingparking.reservation.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationResponseDto {
    private final Integer reservationId;
    private final ParkingLotInfo parkingLot;
    private final VehicleInfo vehicle;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;

    public ReservationResponseDto(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.parkingLot = new ParkingLotInfo(reservation.getParkingSpace().getParkingLot());
        this.vehicle = new VehicleInfo(reservation.getVehicle());
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.status = reservation.getStatus();
    }
}

