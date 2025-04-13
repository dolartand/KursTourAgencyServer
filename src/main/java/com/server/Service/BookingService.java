package com.server.Service;

import com.server.Entities.Booking;
import com.server.Entities.Tour;
import com.server.Repositories.BookingRepository;
import com.server.Repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, TourRepository tourRepository) {
        this.bookingRepository = bookingRepository;
        this.tourRepository = tourRepository;
    }

    public boolean bookTour(int tourId, int userId) {
        Tour tour = tourRepository.findById(tourId).orElse(null);
        if (tour == null) {
            return false;
        }

        int currentBookings = bookingRepository.countByTourId(tourId);
        if (currentBookings >= tour.getCapacity()) {
            return false;
        }

        Booking booking = new Booking();
        booking.setTourId(tourId);
        booking.setUserId(userId);
        bookingRepository.save(booking);
        return true;
    }
}
