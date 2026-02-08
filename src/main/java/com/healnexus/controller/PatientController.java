package com.healnexus.controller;

import com.healnexus.dto.request.PatientProfileRequest;
import com.healnexus.dto.response.APIResponse;
import com.healnexus.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    @PostMapping("/profile")
    public ResponseEntity<APIResponse> completePatientProfile(@Valid @RequestBody PatientProfileRequest patientProfileRequest) {
        patientService.completePatientProfile(patientProfileRequest);
        return new  ResponseEntity<>(new APIResponse("Patient profile created successfully",true),HttpStatus.CREATED);
  }
}
