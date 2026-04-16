package com.example.pickingparking.parkinglot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Integer> {

    @Query(value = "SELECT p.lot_id, p.name, p.address, p.available_spaces, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(p.latitude)))) AS distance " +
            "FROM ParkingLots p " +
            "ORDER BY distance " +
            "LIMIT 3", nativeQuery = true)
    List<Object[]> findNearbyParkingLots(@Param("lat") Double lat, @Param("lng") Double lng);
}


