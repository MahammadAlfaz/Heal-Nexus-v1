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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private static final int MAX_FAILED_ATTEMPTS = 20;
    private  static final long MAX_FAILED_ATTEMPT_DURATION=15;


    @Transactional
    public User registerUser(User user) {
        User existingUser =userRepository.findByEmail(user.getEmail());
        if(existingUser!=null){
            throw new IllegalArgumentException("User already exists");
        }
        return userRepository.save(user);
    }
    public User findByEmail(String email){
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new IllegalArgumentException("User not found");
        }
        return  user;
    }
    @Transactional
    public void increaseFailedLoginAttempts(User user){

        int newFailedAttempt=user.getFailedLoginAttempts()+1;
        user.setFailedLoginAttempts(newFailedAttempt);
        if(newFailedAttempt>=MAX_FAILED_ATTEMPTS){
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
            userRepository.save(user);

        }
 }
 @Transactional
 public boolean unlockIfLockExpires(User user){
        if(!user.isAccountLocked()){
            return false;
        }
        if(!LocalDateTime.now().isAfter(user.getLockTime().plusMinutes(MAX_FAILED_ATTEMPT_DURATION)))
           return false;
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
        return true;


 }
 @Transactional
 public void resetFailedLoginAttempts(User user){
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
 }

}