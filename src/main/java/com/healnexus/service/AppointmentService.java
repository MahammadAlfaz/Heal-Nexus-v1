package com.healnexus.service;

import com.healnexus.dto.request.AppointmentCreateRequest;
import com.healnexus.dto.response.AppointmentResponse;
import com.healnexus.exception.ResourceNotFoundException;
import com.healnexus.model.*;
import com.healnexus.repositories.AppointmentRepository;
import com.healnexus.repositories.DoctorRepository;
import com.healnexus.repositories.PatientRepository;
import com.healnexus.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

@Transactional
    public AppointmentResponse bookAppointment(AppointmentCreateRequest appointmentCreateRequest) {

    User user=userRepository.findById(appointmentCreateRequest.getPatientUserId()).orElseThrow(
            ()->new ResourceNotFoundException("User","userId",appointmentCreateRequest.getPatientUserId()));
    if(user.getRole()!= Role.PATIENT){
        throw new IllegalStateException("Invalid user role");
    }

    Patient patient=patientRepository.findByUser_Id(user.getId()).orElseThrow(
            ()->new ResourceNotFoundException("Patient","id",user.getId())
    );
    if(!patient.isActive()) throw new IllegalStateException("Patient is not active");

    Doctor doctor=doctorRepository.findById(appointmentCreateRequest.getDoctorId()).orElseThrow(
            ()->new ResourceNotFoundException("Doctor","id",appointmentCreateRequest.getDoctorId())
    );
    if(!doctor.isActive()) throw new IllegalStateException("Doctor is not active");

    boolean existingAppointment=appointmentRepository.existsByDoctor_IdAndAppointmentTimeAndAppointmentStatusNot(appointmentCreateRequest.getDoctorId(),
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
        AppointmentResponse appointmentResponse= modelMapper.map(appointment,AppointmentResponse.class);
        appointmentResponse.setDoctorName(doctor.getFullName());
        appointmentResponse.setPatientName(patient.getFullName());
        appointmentResponse.setSpecialization(doctor.getSpecialization());
        return  appointmentResponse;
    }
    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId,Long userId) {
    User  user=userRepository.findById(userId).orElseThrow(
            ()->new ResourceNotFoundException("User","userId",userId)
    );
    if(user.getRole()!= Role.PATIENT){
        throw new IllegalStateException("Invalid user role");
    }
    Patient patient=patientRepository.findByUser_Id(userId).orElseThrow(
            ()->new ResourceNotFoundException("Patient","id",userId)
    );
    if(!patient.isActive()) throw new IllegalStateException("Patient is not active");
    Appointment appointment=appointmentRepository.findById(appointmentId).orElseThrow(
                ()->new ResourceNotFoundException("Appointment","id",appointmentId)
        );
    if(!appointment.getPatient().getUser().getId().equals(user.getId())){
            throw new IllegalStateException("You are not allowed to cancel this appointment");
        }
    if(appointment.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)|| appointment.getAppointmentStatus().equals(AppointmentStatus.COMPLETED)){
        throw new IllegalStateException("Appointment cannot be cancelled");
    }
    appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
    appointmentRepository.save(appointment);


    AppointmentResponse response= modelMapper.map(appointment,AppointmentResponse.class);
    response.setDoctorName(appointment.getDoctor().getFullName());
    response.setPatientName(patient.getFullName());
    response.setSpecialization(appointment.getDoctor().getSpecialization());

    return  response;

    }

   public List<AppointmentResponse> getAppointmentsByPatient(Long patientUserId) {
    User user =userRepository.findById(patientUserId).orElseThrow(
            ()->new ResourceNotFoundException("User","userId",patientUserId)
    );
    if(user.getRole()!= Role.PATIENT){
        throw new IllegalStateException("Invalid user role");
    }
    Patient patient=patientRepository.findByUser_Id(user.getId()).orElseThrow(
            ()->new ResourceNotFoundException("Patient","id",user.getId())
    );
    if(!patient.isActive()) throw new IllegalStateException("Patient is not active");
    List<Appointment> appointments=appointmentRepository.findByPatient_User_IdAndAppointmentStatusNot(patientUserId,AppointmentStatus.CANCELLED);

    List<AppointmentResponse> responses=appointments.stream()
            .map(res-> {
                        AppointmentResponse appointmentResponse =modelMapper.map(res,AppointmentResponse.class);
                        appointmentResponse.setDoctorName(res.getDoctor().getFullName());
                        appointmentResponse.setPatientName(res.getPatient().getFullName());
                        appointmentResponse.setSpecialization(res.getDoctor().getSpecialization());
                        return appointmentResponse;
                    }
            ).toList();
    return responses;
}


    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {

    Doctor doctor=doctorRepository.findById(doctorId).orElseThrow(
            ()->new ResourceNotFoundException("Doctor","id",doctorId)
    );
    if(!doctor.isActive()) throw new IllegalStateException("Doctor is not active");
    List<Appointment> appointments=appointmentRepository.findByDoctor_IdAndAppointmentStatusNot(doctorId,AppointmentStatus.CANCELLED);
        List<AppointmentResponse> responses=appointments.stream()
                .map(res-> {
                            AppointmentResponse appointmentResponse =modelMapper.map(res,AppointmentResponse.class);
                            appointmentResponse.setDoctorName(res.getDoctor().getFullName());
                            appointmentResponse.setPatientName(res.getPatient().getFullName());
                            appointmentResponse.setSpecialization(res.getDoctor().getSpecialization());
                            return appointmentResponse;
                        }
                ).toList();
        return responses;
    }
}
