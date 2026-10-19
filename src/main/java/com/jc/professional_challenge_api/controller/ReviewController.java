package com.jc.professional_challenge_api.controller;

import com.jc.professional_challenge_api.controller.dto.ReviewRequest;
import com.jc.professional_challenge_api.controller.dto.ReviewResponse;
import com.jc.professional_challenge_api.controller.dto.ReviewSubmitResponse;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Reviews of a product: anyone can read them, a logged-in user can publish (see SecurityConfig).
@RestController
@RequestMapping("products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getReviews(productId);
    }

    // 201 with the review and the product's new average/total; 400 if rating is not 1-5.
    @PostMapping
    public ResponseEntity<ReviewSubmitResponse> create(@PathVariable Long productId, @Valid @RequestBody ReviewRequest request,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(user, productId, request));
    }
}
