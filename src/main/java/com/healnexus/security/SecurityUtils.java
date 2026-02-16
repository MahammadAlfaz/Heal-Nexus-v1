package com.healnexus.security;

import com.healnexus.model.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public  String getCurrentUserEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public  Role getUserRole(){
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .filter(auth->auth.startsWith("ROLE_"))
                .map(auth->auth.replace("ROLE_",""))
                .map(auth->Role.valueOf(auth))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("User has no role assigned")
                );
    }

}
