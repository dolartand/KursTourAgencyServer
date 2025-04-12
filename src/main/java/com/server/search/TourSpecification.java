package com.server.search;

import com.server.Entities.Tour;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TourSpecification implements Specification<Tour> {
    private final TourSearchCriteria criteria;

    public TourSpecification(TourSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Tour> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (criteria.getCountry() != null && !criteria.getCountry().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("country")), "%" + criteria.getCountry().toLowerCase() + "%"));
        }

        if (criteria.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), criteria.getStartDate()));
        }

        if (criteria.getNights() != null) {
            predicates.add(cb.equal(root.get("nights"), criteria.getNights()));
        }

        if (criteria.getPersons() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), criteria.getPersons()));
        }

        if (criteria.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
        }

        if (criteria.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
        }

        // Фильтр по питанию
        if (criteria.getFood() != null && !criteria.getFood().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("food"), criteria.getFood()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
