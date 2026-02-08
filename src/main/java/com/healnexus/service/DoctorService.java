package com.healnexus.service;

import com.healnexus.dto.request.DoctorProfileRequest;
import com.healnexus.dto.response.DoctorProfileResponse;
import com.healnexus.exception.ResourceNotFoundException;
import com.healnexus.model.Doctor;
import com.healnexus.model.Role;
import com.healnexus.model.User;
import com.healnexus.repositories.DoctorRepository;
import com.healnexus.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Transactional
    public void completeDoctorProfile(DoctorProfileRequest doctorProfileRequest) {
        User user=userRepository.findById(doctorProfileRequest.getUserId()).orElseThrow(
                ()-> new ResourceNotFoundException("User","id",doctorProfileRequest.getUserId())
        );
        if(user.getRole()!= Role.DOCTOR){
            throw new IllegalArgumentException("User is not allowed to create a doctor profile");
        }
        if(doctorRepository.existsByUser_Id(user.getId())){
            throw new IllegalStateException("Doctor already exists");
        }

        Doctor doctor= Doctor.builder()
                .user(user)
                .fullName(doctorProfileRequest.getFullName())
                .phoneNumber(doctorProfileRequest.getPhoneNumber())
                .specialization( doctorProfileRequest.getSpecialization())
                .licenseNumber(doctorProfileRequest.getLicenseNumber())
                .profileImageUrl(doctorProfileRequest.getProfileImageUrl())
                .yearsOfExperience(doctorProfileRequest.getYearsOfExperience())
                .active(true)
                .build();
        doctorRepository.save(doctor);
    }

    public DoctorProfileResponse getDoctorProfile(Long userId) {

        User user=userRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFoundException("User","id",userId)
        );
        Doctor doctor=doctorRepository.findByUser(user);
        if(doctor==null){
            throw new ResourceNotFoundException("Doctor","id",userId);
        }


      return modelMapper.map(doctor,DoctorProfileResponse.class);

    }
}
