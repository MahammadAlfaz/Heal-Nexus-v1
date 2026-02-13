package com.healnexus.controller;

import com.healnexus.dto.request.AppointmentCreateRequest;
import com.healnexus.dto.response.AppointmentResponse;
import com.healnexus.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
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
            @PathVariable Long appointmentId,
            @RequestParam Long patientUserId
    ) {
        AppointmentResponse response =
                appointmentService.cancelAppointment(appointmentId, patientUserId);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/patient/{patientUserId}")
    public  ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(@PathVariable Long patientUserId) {
        List<AppointmentResponse> responses=appointmentService.getAppointmentsByPatient(patientUserId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(@PathVariable Long doctorId){
        List<AppointmentResponse> responses=appointmentService.getAppointmentsByDoctor(doctorId);
        return new ResponseEntity<>(responses, HttpStatus.OK);


    }


}
