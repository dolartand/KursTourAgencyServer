package com.server.Service;

import com.server.Entities.Tour;
import com.server.Repositories.TourRepository;
import com.server.search.TourSearchCriteria;
import com.server.search.TourSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourService {
    private final TourRepository tourRepository;

    @Autowired
    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
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
}
