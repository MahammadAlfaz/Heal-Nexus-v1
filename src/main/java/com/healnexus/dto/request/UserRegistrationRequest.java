package com.healnexus.dto.request;

import com.healnexus.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {
    @NotBlank
    @Email(message = "Invalid email address ")
    private String email;
    @NotBlank
    @Size(min = 8 ,message = "password must be at least 8 character")
    private String password;
    @NotNull
    private Role role;

}
