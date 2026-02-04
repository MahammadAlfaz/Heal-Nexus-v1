package com.healnexus.service;
import com.healnexus.model.Doctor;
import com.healnexus.model.Patient;
import com.healnexus.model.Role;
import com.healnexus.model.User;
import com.healnexus.repositories.DoctorRepository;
import com.healnexus.repositories.PatientRepository;
import com.healnexus.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    @Transactional
    public User registerUser(User user){
        User savedUser= userRepository.save(user);

        if(savedUser.getRole()== Role.PATIENT){
            createPatient(savedUser);

        }
        else if (savedUser.getRole()== Role.DOCTOR){
            createDoctor(savedUser);
        }
        return savedUser;
    }

    private void createPatient(User user){
        Patient patient=new Patient();
        patient.setUser(user);
        patient.setActive(true);
        patientRepository.save(patient);

    }

    private void createDoctor(User user){
        Doctor doctor=new Doctor();
        doctor.setUser(user);
        doctor.setActive(true);
        doctorRepository.save(doctor);
    }
}
