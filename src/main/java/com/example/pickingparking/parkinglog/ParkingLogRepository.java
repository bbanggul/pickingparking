package com.example.pickingparking.parkinglog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLogRepository extends JpaRepository<ParkingLog, Integer> {
}

