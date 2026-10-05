package com.jc.professional_challenge_api.controller.dto;

public record LoginResponse(String token, UserResponse user) {
}
