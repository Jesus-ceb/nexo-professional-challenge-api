package com.jc.professional_challenge_api.controller.dto;

import com.jc.professional_challenge_api.entities.Role;

public record UserResponse(Long id, String name, String lastName, String email, Role role) {
}
