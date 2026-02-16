package com.healnexus.security.refresh;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long REFRESH_EXPIRY_DAY= 7;
    @Transactional
    public RefreshToken createRefreshToken(String email){
        refreshTokenRepository.deleteByEmail(email);
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken=RefreshToken.builder()
                .token(token)
                .email(email)
                .expiredAt(LocalDateTime.now().plusDays(REFRESH_EXPIRY_DAY))
                . build();
        return refreshTokenRepository.save(refreshToken);

    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken=refreshTokenRepository.findByToken(token).orElseThrow(
        ()->new RuntimeException("Invalid token")
        );
        if(refreshToken.getExpiredAt().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException(" token expired");
        }
        return refreshToken;
    }

    @Transactional
    public void deleteRefreshToken(String token){
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }


}
