package com.healnexus.service;


import com.healnexus.audit.Audit;
import com.healnexus.dto.request.PatientProfileRequest;
import com.healnexus.dto.response.PatientProfileResponse;
import com.healnexus.exception.ResourceNotFoundException;
import com.healnexus.model.Patient;
import com.healnexus.model.User;
import com.healnexus.repositories.PatientRepository;
import com.healnexus.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Audit(action = "PATIENT_REGISTERED")
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

    public PatientProfileResponse getPatientProfile(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User","userId",userId));
        Patient patient=patientRepository.findByUser(user);
        if(patient==null){
            throw new ResourceNotFoundException("Patient","userId",userId);
        }
        return modelMapper.map(patient,PatientProfileResponse.class);

    }
}
