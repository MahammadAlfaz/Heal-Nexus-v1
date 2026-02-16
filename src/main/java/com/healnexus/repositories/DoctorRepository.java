package com.healnexus.repositories;

import com.healnexus.model.Doctor;
import com.healnexus.model.Role;
import com.healnexus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {



    boolean existsByUser_Id(Long id);




    Doctor findByUser(User user);

    Optional<Doctor> findByUser_Email(String loggedInEmail);
}
