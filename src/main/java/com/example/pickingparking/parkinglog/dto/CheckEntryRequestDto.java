package com.example.pickingparking.parkinglog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckEntryRequestDto {
    private Integer spaceId;
    private String licensePlate;
}

