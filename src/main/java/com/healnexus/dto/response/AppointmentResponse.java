package com.healnexus.dto.response;

import com.healnexus.model.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long appointmentId;

    private Long doctorId;
    private String doctorName;
    private String specialization;

    private Long patientId;
    private String patientName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus  appointmentStatus;
    private String reason;

    private LocalDateTime createdAt;
}
