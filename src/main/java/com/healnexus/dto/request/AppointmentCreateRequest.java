package com.healnexus.dto.request;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentCreateRequest {
    @NotNull
    private Long doctorId;

    @FutureOrPresent
    @NotNull
    private LocalDateTime appointmentTime;
    @NotBlank
    @Length(max=100)
    private String reason;
}
