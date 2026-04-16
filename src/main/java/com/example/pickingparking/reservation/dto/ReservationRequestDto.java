package com.example.pickingparking.reservation.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationRequestDto {
    private Integer spaceId;
    private Integer vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
