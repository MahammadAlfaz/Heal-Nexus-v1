package com.healnexus.security.refresh;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @Column( nullable = false)
    private String email;
    @Column( nullable = false)
    private LocalDateTime expiredAt;

}
