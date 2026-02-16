package com.healnexus.repositories;

import com.healnexus.model.Appointment;
import com.healnexus.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment,Long> {
    boolean existsByDoctor_IdAndAppointmentTimeAndAppointmentStatusNot(Long doctorId, LocalDateTime appointmentTime, AppointmentStatus appointmentStatus);

    Page<Appointment> findByDoctor_IdAndAppointmentStatusNot(Long doctorId, AppointmentStatus appointmentStatus, Pageable pageable);

    Page<Appointment> findByPatient_IdAndAppointmentStatusNot(Long id, AppointmentStatus appointmentStatus,Pageable pageable);
}
