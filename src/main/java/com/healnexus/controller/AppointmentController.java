package com.healnexus.controller;

import com.healnexus.dto.request.AppointmentCreateRequest;
import com.healnexus.dto.response.AppointmentResponse;
import com.healnexus.dto.response.PaginationResponse;
import com.healnexus.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private  final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentCreateRequest
                                                               appointmentCreateRequest){
        AppointmentResponse response=appointmentService.bookAppointment(appointmentCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long appointmentId
    ) {
        AppointmentResponse response =
                appointmentService.cancelAppointment(appointmentId );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{appointmentId}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment( @PathVariable Long appointmentId){
        AppointmentResponse response=appointmentService.confirmAppointment(appointmentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment( @PathVariable Long appointmentId){
        AppointmentResponse response=appointmentService.completeAppointment(appointmentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/patient/me")
    public  ResponseEntity<PaginationResponse<AppointmentResponse>> getAppointmentsByPatient(Pageable pageable) {
        PaginationResponse<AppointmentResponse> responses=appointmentService.getAppointmentsByPatient(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/doctor/me")
    public ResponseEntity<PaginationResponse<AppointmentResponse>> getAppointmentsByDoctor(Pageable pageable) {
        PaginationResponse<AppointmentResponse> responses=appointmentService.getAppointmentsByDoctor(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);


    }


}
