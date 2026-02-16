package com.healnexus.repositories;

import com.healnexus.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(@Email String email);

    Optional<User> findByEmailIgnoreCase(String userEmail);
}