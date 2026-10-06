package com.jc.professional_challenge_api.controller.dto;

import jakarta.validation.constraints.NotBlank;

// Body of POST/PUT /features: { "name": "Wifi", "icon": "ri-wifi-line" }.
public record FeatureRequest(@NotBlank String name, @NotBlank String icon) {
}
