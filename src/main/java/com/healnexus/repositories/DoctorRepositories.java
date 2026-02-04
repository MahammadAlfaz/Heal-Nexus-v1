package com.healnexus.repositories;

import com.healnexus.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepositories extends JpaRepository<Doctor,Long> {
}
