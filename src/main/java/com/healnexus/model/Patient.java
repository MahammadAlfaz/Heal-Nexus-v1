package com.healnexus.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients")
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id",
            nullable = false,
            unique = true)
    private  User user;


    @Column(nullable = false)
    private  String fullName;

    private LocalDate birthDate;

    @Column(nullable = false ,length = 20)
    private String phoneNumber;

    @Column(nullable = false ,length = 20)
    private String emergencyContact;

    private String gender;

    private  String bloodGroup;
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false ,length = 300)
    private String address;

    private String allergies;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected  void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected  void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }



}
