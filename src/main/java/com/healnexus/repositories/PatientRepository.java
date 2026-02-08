package com.healnexus.repositories;

import com.healnexus.model.Patient;
import com.healnexus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    Patient findByUser(User user);
}
