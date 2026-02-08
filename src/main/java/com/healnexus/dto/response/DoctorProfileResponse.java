package com.healnexus.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {
    private Long userId;

    private Long doctorId;

    private String fullName;

    private String profileImageUrl;


    private String specialization;


    private String licenseNumber;


    private String phoneNumber;

    private Integer yearsOfExperience;
}
