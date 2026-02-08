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


    @Transactional
    public User registerUser(User user) {
        User existingUser =userRepository.findByEmail(user.getEmail());
        if(existingUser!=null){
            throw new IllegalArgumentException("User already exists");
        }
        return userRepository.save(user);
    }
}