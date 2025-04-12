package com.server.Repositories;

import com.server.Entities.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer>, JpaSpecificationExecutor<Tour> {
    List<Tour> findByCountryAndPriceBetween(String country, double minPrice, double maxPrice);
}
