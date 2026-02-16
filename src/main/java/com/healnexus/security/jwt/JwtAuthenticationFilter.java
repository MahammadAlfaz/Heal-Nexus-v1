package com.healnexus.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header=request.getHeader("Authorization");
        System.out.println("FILTER RUNNING for: " + request.getRequestURI());

        if(header==null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token=header.substring(7);

        try{
            String email = jwtService.extractEmail(token);


            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);



                boolean validateToken =
                        jwtService.validateToken(token, userDetails);



                if (validateToken) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                }
            }



    }catch(Exception ex){
            ex.printStackTrace();
        }
        filterChain.doFilter(request,response);

    }
}
