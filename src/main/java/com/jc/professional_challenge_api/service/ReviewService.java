package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.ReviewRequest;
import com.jc.professional_challenge_api.controller.dto.ReviewResponse;
import com.jc.professional_challenge_api.controller.dto.ReviewSubmitResponse;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.entities.Review;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.repository.ProductRepository;
import com.jc.professional_challenge_api.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    //Reviews of a product, newest first. 404 if the product doesn't exist.
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long productId) {
        productService.findById(productId);
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream().map(ReviewResponse::from).toList();
    }

    //Publishes a new review (a user can leave several) and stores the product's new average and total.
    @Transactional
    public ReviewSubmitResponse create(User user, Long productId, ReviewRequest request) {
        // TODO: when bookings exist, only users with a finished booking of this product can review it.
        Product product = productService.findById(productId);

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        String comment = request.comment() == null ? null : request.comment().trim();
        review.setComment(comment == null || comment.isEmpty() ? null : comment);
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);

        Double average = reviewRepository.averageRating(productId);
        int count = (int) reviewRepository.countByProductId(productId);
        // 2 decimals so the frontend can show it out of 10 without losing precision (4.33 -> 8.7)
        product.setAverageRating(average == null ? null : Math.round(average * 100) / 100.0);
        product.setReviewCount(count);
        productRepository.save(product);

        return new ReviewSubmitResponse(ReviewResponse.from(saved), product.getAverageRating(), count);
    }
}
