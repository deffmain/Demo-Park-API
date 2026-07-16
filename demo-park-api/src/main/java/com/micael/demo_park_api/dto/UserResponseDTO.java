package com.micael.demo_park_api.dto;

import com.micael.demo_park_api.domain.User;

public record UserResponseDTO(Long idUser, String name, User.Role role) {
}
