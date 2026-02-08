package com.healnexus.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorProfileRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String fullName;

    private String profileImageUrl;
    @NotBlank
    private String specialization;
    @NotBlank
    private  String licenseNumber;
    @NotBlank
    private String phoneNumber;
    @NotNull
    @Min( value=0,message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

}
