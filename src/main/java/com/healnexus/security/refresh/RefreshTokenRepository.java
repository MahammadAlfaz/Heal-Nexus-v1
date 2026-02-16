package com.healnexus.security.refresh;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    void deleteByEmail(String email);

    Optional<RefreshToken> findByToken(String token);
}
