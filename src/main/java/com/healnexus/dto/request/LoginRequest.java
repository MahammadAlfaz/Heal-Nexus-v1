package com.healnexus.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @Email(message = "Invalid email format ")
    @NotBlank(message = "email can't be blank ")
    private String email;
    @NotBlank(message = "password can't be blank")
    @Size(min = 6,message = "password should be more than 5 character ")
    private String password;
}
