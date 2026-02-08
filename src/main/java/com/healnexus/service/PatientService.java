package com.healnexus.service;


import com.healnexus.dto.request.PatientProfileRequest;
import com.healnexus.exception.ResourceNotFoundException;
import com.healnexus.model.Patient;
import com.healnexus.model.User;
import com.healnexus.repositories.PatientRepository;
import com.healnexus.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    @Transactional
    public void completePatientProfile(PatientProfileRequest patientProfileRequest) {
        User user=userRepository.findById(patientProfileRequest.getUserId()).
                orElseThrow(()->new IllegalArgumentException("User not found"));
        Patient existingPatient=patientRepository.findByUser(user);
        if(existingPatient!=null){
            throw new IllegalArgumentException("Patient already exists");
        }

         Patient patient =Patient.builder()
                 .user(user)
                 .fullName(patientProfileRequest.getFullName())
                 .address(patientProfileRequest.getAddress())
                 .allergies(patientProfileRequest.getAllergies())
                 .phoneNumber(patientProfileRequest.getPhoneNumber())
                 .birthDate(patientProfileRequest.getBirthDate())
                 .gender(patientProfileRequest.getGender())
                 .bloodGroup(patientProfileRequest.getBloodGroup())
                 .emergencyContact(patientProfileRequest.getEmergencyContact())
                 .active(true)
                 .build();
         patientRepository.save(patient);
    }

}
