package com.server.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "tour_id", nullable = false)
    private int tourId;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate = LocalDate.now();

    @Column(name = "status", nullable = false)
    private String status = "PENDING";
}
