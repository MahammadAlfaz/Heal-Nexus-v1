package com.healnexus.repositories;

import com.healnexus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositories extends JpaRepository<User,Long> {

}
