package com.reer.resumebuilder.resume_builder_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 30 characters")
    private String name;

    @Email(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max=15, message = "Password must be at least 6  and at most 15 characters long")
    private String password;


    private String profileImageUrl;
}
