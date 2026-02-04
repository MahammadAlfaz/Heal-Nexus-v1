package com.healnexus.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(optional = false)
    @JoinColumn(name = "user_id",
    nullable = false,unique = true
    )
    private User user;


    @Column(nullable = false)
    private String fullName;

    private String profileImageUrl;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false,unique = true)
    private String licenseNumber;

    @Column(length = 20, nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private Integer yearsOfExperience;


    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;


    @Column(nullable = false)
     private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    protected  void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected  void onUpdate(){
        this.updatedAt = LocalDateTime.now();

    }


}