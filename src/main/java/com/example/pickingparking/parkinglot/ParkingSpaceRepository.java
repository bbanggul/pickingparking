package com.example.pickingparking.parkinglot;

import com.example.pickingparking.reservation.Reservation;
import com.example.pickingparking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Integer> {

}
