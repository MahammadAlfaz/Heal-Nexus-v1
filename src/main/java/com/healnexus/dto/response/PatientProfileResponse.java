package com.healnexus.dto.response;

import com.healnexus.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfileResponse {
    private Long userId;


    private Long patientId;



    private  String fullName;

    private LocalDate birthDate;


    private String phoneNumber;


    private String emergencyContact;

    private String gender;

    private  String bloodGroup;
    private boolean active = true;


    private String address;

    private String allergies;
}
