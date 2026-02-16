package com.healnexus.service;
import com.healnexus.audit.Audit;
import com.healnexus.dto.request.AppointmentCreateRequest;
import com.healnexus.dto.response.AppointmentResponse;
import com.healnexus.dto.response.PaginationResponse;
import com.healnexus.exception.ResourceNotFoundException;
import com.healnexus.model.*;
import com.healnexus.repositories.AppointmentRepository;
import com.healnexus.repositories.DoctorRepository;
import com.healnexus.repositories.PatientRepository;
import com.healnexus.security.SecurityUtils;
import com.healnexus.util.PaginationHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtils securityUtils;


    @Audit(action = "BOOK_APPOINTMENT")
     @Transactional
     @PreAuthorize("hasRole('PATIENT')")
    public AppointmentResponse bookAppointment(AppointmentCreateRequest appointmentCreateRequest) {

     String loggedInUser= securityUtils.getCurrentUserEmail();
    Patient patient=patientRepository.findByUser_Email(loggedInUser).orElseThrow(
            ()->new ResourceNotFoundException("Patient","email",loggedInUser)
    );
    if(!patient.isActive()) throw new IllegalStateException("Patient is not active");
    Doctor doctor=doctorRepository.findById(appointmentCreateRequest.getDoctorId()).orElseThrow(
            ()->new ResourceNotFoundException("Doctor","id",appointmentCreateRequest.getDoctorId())
    );
    if(!doctor.isActive()) throw new IllegalStateException("Doctor is not active");
    boolean existingAppointment=appointmentRepository.existsByDoctor_IdAndAppointmentTimeAndAppointmentStatusNot(doctor.getId(),
            appointmentCreateRequest.getAppointmentTime(),
            AppointmentStatus.CANCELLED);
    if(existingAppointment){
        throw new IllegalStateException("Doctor already booked for this time slot");
    }
    Appointment appointment=Appointment.builder()
            .appointmentStatus(AppointmentStatus.BOOKED)
            .doctor(doctor)
            .patient(patient)
            .appointmentTime(appointmentCreateRequest.getAppointmentTime())
            .reason(appointmentCreateRequest.getReason())
            .build();
    appointmentRepository.save(appointment);
        return  mapToResponse(appointment);
    }





    @Audit(action = "CANCEL_APPOINTMENT")
    @Transactional
    @PreAuthorize("hasAnyRole('DOCTOR','PATIENT','ADMIN')")
    public AppointmentResponse cancelAppointment(Long appointmentId) {
    Appointment appointment=appointmentRepository.findById(appointmentId).orElseThrow(
            ()->new ResourceNotFoundException("Appointment","id",appointmentId)
    );
    String currentUserEmail=securityUtils.getCurrentUserEmail();
    Role role=securityUtils.getUserRole();
    switch(role){
        case PATIENT -> {if(!appointment.getPatient()
                .getUser()
                .getEmail()
                .equalsIgnoreCase(currentUserEmail))

            throw new AccessDeniedException("You can cancel only your own appointments");
        }
        case DOCTOR ->{

         if(!appointment.getDoctor().getUser().getEmail().equalsIgnoreCase(currentUserEmail))
            throw new AccessDeniedException("You can cancel only your assigned appointments");
        }
        case ADMIN, HOSPITAL -> {

        }
        default -> throw new AccessDeniedException("Unauthorized role");
    }
    if(appointment.getAppointmentStatus()==(AppointmentStatus.CANCELLED)|| appointment.getAppointmentStatus()==(AppointmentStatus.COMPLETED)){
        throw new IllegalStateException("Appointment cannot be cancelled");
    }
    appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
    appointmentRepository.save(appointment);


    return  mapToResponse(appointment);

    }





    @PreAuthorize("hasRole('PATIENT')")
   public PaginationResponse<AppointmentResponse> getAppointmentsByPatient(Pageable pageable) {

        String currentUserEmail = securityUtils.getCurrentUserEmail();
        Pageable normalizedPageable=PaginationHelper.paginationNormalize(pageable);
        Patient patient = patientRepository.findByUser_Email(currentUserEmail).orElseThrow(
                () -> new ResourceNotFoundException("Patient", "email", currentUserEmail)
        );
        if(!patient.isActive()){
            throw new IllegalStateException("Patient is not active");
        }
            Page<Appointment> appointmentPage = appointmentRepository.findByPatient_IdAndAppointmentStatusNot(patient.getId(), AppointmentStatus.CANCELLED,normalizedPageable);
            List<AppointmentResponse> data = appointmentPage.getContent().stream()
                    .map(this::mapToResponse)
                    .toList();
            return PaginationHelper.buildPaginationResponse(appointmentPage, data);
        }





 @PreAuthorize("hasRole('DOCTOR')")
    public PaginationResponse<AppointmentResponse> getAppointmentsByDoctor(Pageable pageable) {

   String loggedInUserEmail = securityUtils.getCurrentUserEmail();

  Pageable normalizedPageable=PaginationHelper.paginationNormalize(pageable);
   Doctor doctor=doctorRepository.findByUser_Email(loggedInUserEmail).orElseThrow(
           ()->new ResourceNotFoundException("Doctor","email",loggedInUserEmail)
   );
    if(!doctor.isActive()) throw new IllegalStateException("Doctor is not active");
    Page<Appointment> appointmentPage=appointmentRepository.findByDoctor_IdAndAppointmentStatusNot(doctor.getId(),AppointmentStatus.CANCELLED,normalizedPageable);
        List<AppointmentResponse> data=appointmentPage.getContent().stream()
                .map(this::mapToResponse).toList();
        return PaginationHelper.buildPaginationResponse(appointmentPage,data);
    }





    @Audit(action = "CONFIRM_APPOINTMENT")
    @Transactional
     @PreAuthorize("hasRole('DOCTOR')")
    public AppointmentResponse confirmAppointment(Long appointmentId) {
    String loggedInUserEmail = securityUtils.getCurrentUserEmail();

    Appointment appointment=appointmentRepository.findById(appointmentId).orElseThrow(
            ()->new ResourceNotFoundException("Appointment","id",appointmentId)
    );
    if(!appointment.getDoctor().getUser().getEmail().equalsIgnoreCase(loggedInUserEmail)){
        throw new AccessDeniedException("Invalid appointment");
    }
    if(appointment.getAppointmentStatus()!=(AppointmentStatus.BOOKED)){
        throw new IllegalStateException("Appointment cannot be confirmed");
    }
    appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);

    return  mapToResponse(appointment);
    }





    @Audit(action = "COMPLETE_APPOINTMENT")
    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    public AppointmentResponse completeAppointment(Long appointmentId) {
        String loggedInUserEmail = securityUtils.getCurrentUserEmail();

        Appointment appointment=appointmentRepository.findById(appointmentId).orElseThrow(
                ()->new ResourceNotFoundException("Appointment","id",appointmentId)
        );
        if(!appointment.getDoctor().getUser().getEmail().equalsIgnoreCase(loggedInUserEmail)){
            throw new AccessDeniedException("Invalid appointment");
        }
        if(appointment.getAppointmentStatus()!=(AppointmentStatus.CONFIRMED)){
            throw new IllegalStateException("Appointment cannot be completed");
        }
        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);

        return  mapToResponse(appointment);
    }





    public AppointmentResponse mapToResponse(Appointment appointment){
        AppointmentResponse response=modelMapper.map(appointment,AppointmentResponse.class);
        response.setDoctorName(appointment.getDoctor().getFullName());
        response.setPatientName(appointment.getPatient().getFullName());
        response.setSpecialization(appointment.getDoctor().getSpecialization());
        return response;
    }
}
