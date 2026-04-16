package com.example.pickingparking.reservation;

import com.example.pickingparking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.parkingSpace.spaceId = :spaceId " +
            "AND r.startTime <= :currentTime " +
            "AND r.endTime >= :currentTime " +
            "AND r.status = 'active'")
    Optional<Reservation> findActiveReservationForSpace(@Param("spaceId") Integer spaceId, @Param("currentTime") LocalDateTime currentTime);

    List<Reservation> findByUserOrderByStartTimeDesc(User user);

    List<Reservation> findAllByEndTimeBeforeAndStatus(LocalDateTime now, String active);
}

