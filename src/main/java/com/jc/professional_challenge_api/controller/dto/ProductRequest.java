package com.jc.professional_challenge_api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

// Body of POST/PUT /products. Only the fields the client may set: id and images are controlled by the backend.
// { "name": "Hotel Nexo", "description": "...", "categoryId": 1, "cityId": 1, "address": "Carrera 10 #20-30", "featureIds": [1, 3] }
public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank String description,
        @NotNull Long categoryId,
        @NotNull Long cityId,
        @NotBlank String address,
        Set<Long> featureIds
) {}
