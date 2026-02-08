package com.healnexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class PatientProfileRequest {

    @NotNull
    private Long userId;
    @NotBlank
    private String fullName;

    private LocalDate birthDate;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String emergencyContact;

    private String gender;

    private String bloodGroup;
    @NotBlank
    private String address;

    private String allergies;
}
