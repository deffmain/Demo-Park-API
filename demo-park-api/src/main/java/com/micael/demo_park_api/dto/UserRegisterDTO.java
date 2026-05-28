package com.micael.demo_park_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO
    (@Email(message = "Email inválido") @NotBlank String username,
     @NotBlank @Size(min = 6, max = 6) String password) {}

