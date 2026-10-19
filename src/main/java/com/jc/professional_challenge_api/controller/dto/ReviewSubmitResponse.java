package com.jc.professional_challenge_api.controller.dto;

// Answer to a new review: the review itself plus the product's updated average and total,
// so the frontend refreshes them at once.
public record ReviewSubmitResponse(ReviewResponse review, Double averageRating, int reviewCount) {
}
