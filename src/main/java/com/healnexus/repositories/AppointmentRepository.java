package com.healnexus.repositories;

import com.healnexus.model.Appointment;
import com.healnexus.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment,Long> {
    boolean existsByDoctor_IdAndAppointmentTimeAndAppointmentStatusNot(Long doctorId, LocalDateTime appointmentTime, AppointmentStatus appointmentStatus);
}
