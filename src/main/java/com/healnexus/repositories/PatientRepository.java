package com.healnexus.repositories;

import com.healnexus.model.Patient;
import com.healnexus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {
    Patient findByUser(User user);

    Optional<Patient> findByUser_Id(Long userId);
}
