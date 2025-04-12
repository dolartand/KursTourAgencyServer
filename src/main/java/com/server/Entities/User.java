package com.server.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "login", nullable = false, unique=true)
    private String login;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "role" ,nullable = false)
    private String role;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "surname")
    private String surname;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String  phoneNumber;
    @Column(name = "birth_date")
    private LocalDate birthDate;
}
