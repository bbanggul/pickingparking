package com.example.pickingparking.parkinglot;

import com.example.pickingparking.parkinglot.dto.ParkingLotDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.pickingparking.parkinglot.dto.ParkingLotDetailDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {

    private final ParkingLotRepository parkingLotRepository;

    @Autowired
    public ParkingLotController(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    @GetMapping("/{lotId}")
    public ResponseEntity<ParkingLotDetailDto> getParkingLotDetails(@PathVariable Integer lotId) {
        ParkingLot parkingLot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주차장을 찾을 수 없습니다."));

        ParkingLotDetailDto responseDto = new ParkingLotDetailDto(parkingLot);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotDto>> getNearbyParkingLots(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {

        List<Object[]> results = parkingLotRepository.findNearbyParkingLots(latitude, longitude);

        List<ParkingLotDto> dtos = results.stream()
                .map(result -> new ParkingLotDto(
                        ((Number) result[0]).intValue(),
                        (String) result[1],
                        (String) result[2],
                        ((Number) result[3]).intValue(),
                        ((Number) result[4]).doubleValue()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

}


