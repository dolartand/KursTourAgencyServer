package com.server.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "tours")
@Data
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", length = 1000)
    private String description;
    @Column(name = "country", nullable = false)
    private String country;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "nights", nullable = false)
    private int nights;
    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "food", nullable = false)
    private String food;
    @Column(name = "capacity", nullable = false)
    private int capacity;
}
