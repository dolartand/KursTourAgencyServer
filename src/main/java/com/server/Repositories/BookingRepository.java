package com.server.Repositories;

import com.server.Entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    int countByTourId(int tourId);
}
