package com.healnexus.repositories;

import com.healnexus.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepositories extends JpaRepository<Patient,Long> {
}
