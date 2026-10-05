package com.jc.professional_challenge_api.controller.dto;

import com.jc.professional_challenge_api.entities.Role;
import jakarta.validation.constraints.NotNull;

// Body of PATCH /users/{id}/role: { "role": "ADMIN" } or { "role": "USER" }.
public record UserRoleRequest(@NotNull Role role) {
}
