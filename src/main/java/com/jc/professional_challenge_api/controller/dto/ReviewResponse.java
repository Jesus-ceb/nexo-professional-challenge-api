package com.jc.professional_challenge_api.controller.dto;

import com.jc.professional_challenge_api.entities.Review;
import com.jc.professional_challenge_api.entities.User;

import java.time.LocalDateTime;

// A published review. userName is "Mario B." (name + last name initial), never the email.
public record ReviewResponse(Long id, int rating, String comment, LocalDateTime createdAt, String userName) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getRating(), review.getComment(),
                review.getCreatedAt(), displayName(review.getUser()));
    }

    private static String displayName(User user) {
        String lastName = user.getLastName();
        if (lastName == null || lastName.isBlank()) return user.getName();
        return user.getName() + " " + lastName.trim().charAt(0) + ".";
    }
}
