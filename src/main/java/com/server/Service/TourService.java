package com.server.Service;

import com.server.Entities.Tour;
import com.server.Repositories.BookingRepository;
import com.server.Repositories.TourRepository;
import com.server.search.TourSearchCriteria;
import com.server.search.TourSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public TourService(TourRepository tourRepository, BookingRepository bookingRepository) {
        this.tourRepository = tourRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Tour> searchTours(TourSearchCriteria criteria) {
        TourSpecification specification = new TourSpecification(criteria);
        return tourRepository.findAll(specification);
    }

    public Tour getTourById(int id) {
        return tourRepository.findById(id).orElse(null);
    }

    public boolean bookTour(int tourId, int userId) {
        return true;
    }

    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    @Transactional
    public boolean deleteTourById(int tourId) {
        if (!tourRepository.existsById(tourId)) {
            return false;
        }
        bookingRepository.deleteByTourId(tourId);
        tourRepository.deleteById(tourId);
        return true;
    }

    public Tour findTourById(int tourId) {
        return tourRepository.findById(tourId).orElse(null);
    }
}
