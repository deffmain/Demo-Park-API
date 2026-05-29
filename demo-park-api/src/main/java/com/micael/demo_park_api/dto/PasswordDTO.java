package com.micael.demo_park_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordDTO(
    @NotBlank @Size(min = 6, max = 6) String currentPassword,
    @NotBlank @Size(min = 6, max = 6) String newPassword,
    @NotBlank @Size(min = 6, max = 6) String confirmNewPassword) {}

