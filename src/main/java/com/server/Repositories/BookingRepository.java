package com.server.Repositories;

import com.server.Entities.Booking;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    int countByTourId(int tourId);

    List<Booking> findByUserId(int userId);

    @Modifying
    @Transactional
    void deleteByTourId(@Param("tourId") int tourId);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :bookingId")
    int updateStatus(@Param("bookingId") int bookingId, @Param("status") String status);
}
