package com.server.Service;

import com.kurs.dto.BookingDTO;
import com.server.Entities.Booking;
import com.server.Entities.Tour;
import com.server.Repositories.BookingRepository;
import com.server.Repositories.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<BookingDTO> getBookingByUserId(int userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        List<BookingDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : bookings) {
            Tour tour = tourRepository.findById(booking.getTourId()).orElse(null);
            if (tour != null) {
                BookingDTO dto = new BookingDTO();
                dto.setBookingID(booking.getId());
                dto.setBookingDate(booking.getBookingDate());
                dto.setTourId(tour.getId());
                dto.setTitle(tour.getTitle());
                dto.setDescription(tour.getDescription());
                dto.setPrice(tour.getPrice());
                dto.setCountry(tour.getCountry());
                dto.setFood(tour.getFood());
                dto.setNights(tour.getNights());
                dto.setPrice(tour.getPrice());
                dto.setStartDate(tour.getStartDate());
                bookingDTOs.add(dto);
            }
        }
        return bookingDTOs;
    }
}
