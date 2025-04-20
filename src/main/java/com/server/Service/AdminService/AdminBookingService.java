package com.server.Service.AdminService;

import com.server.Entities.Booking;
import com.server.Entities.Tour;
import com.server.Entities.User;
import com.server.Repositories.BookingRepository;
import com.server.Repositories.TourRepository;
import com.server.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    @Autowired
    public AdminBookingService(BookingRepository bookingRepository, UserRepository userRepository, TourRepository tourRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.tourRepository = tourRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public boolean approveBooking(int bookingId) {
        return bookingRepository.updateStatus(bookingId, "APPROVED") == 1;
    }

    @Transactional
    public boolean rejectBooking(int bookingId) {
        return bookingRepository.updateStatus(bookingId, "REJECTED") == 1;
    }

    public User findUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public Tour findTourById(int tourId) {
        return tourRepository.findById(tourId).orElse(null);
    }
}
