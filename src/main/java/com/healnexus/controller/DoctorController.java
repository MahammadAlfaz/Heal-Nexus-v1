package com.healnexus.controller;

import com.healnexus.dto.request.DoctorProfileRequest;
import com.healnexus.dto.response.APIResponse;
import com.healnexus.dto.response.DoctorProfileResponse;
import com.healnexus.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    @PostMapping("/profile")
    public ResponseEntity<APIResponse> completeDoctorProfile(@Valid @RequestBody DoctorProfileRequest doctorProfileRequest) {
        doctorService.completeDoctorProfile(doctorProfileRequest);
        return new ResponseEntity<>(new APIResponse("Doctor profile completed successfully",true), HttpStatus.CREATED);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<DoctorProfileResponse> getDoctorProfile(@PathVariable Long userId) {

        DoctorProfileResponse doctorProfileResponse = doctorService.getDoctorProfile(userId);
        return new ResponseEntity<>(doctorProfileResponse, HttpStatus.OK);

    }
}
