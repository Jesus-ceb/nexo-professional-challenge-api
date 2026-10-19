package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.ReviewRequest;
import com.jc.professional_challenge_api.controller.dto.ReviewSubmitResponse;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.entities.Review;
import com.jc.professional_challenge_api.entities.User;
import com.jc.professional_challenge_api.exception.ResourceNotFoundException;
import com.jc.professional_challenge_api.repository.ProductRepository;
import com.jc.professional_challenge_api.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ReviewService reviewService;

    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Hotel Marriot");

        user = new User();
        user.setId(1L);
        user.setName("Mario");
        user.setLastName("Broos");

        // save returns the same review it receives
        lenient().when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void create_updatesTheProductAverageAndTotal() {
        when(productService.findById(1L)).thenReturn(product);
        when(reviewRepository.averageRating(1L)).thenReturn(4.333);
        when(reviewRepository.countByProductId(1L)).thenReturn(3L);

        ReviewSubmitResponse result = reviewService.create(user, 1L, new ReviewRequest(5, "  Muy limpio  "));

        assertEquals(4.33, result.averageRating());
        assertEquals(3, result.reviewCount());
        assertEquals(4.33, product.getAverageRating());
        assertEquals("Muy limpio", result.review().comment());
        assertEquals("Mario B.", result.review().userName());
        verify(productRepository).save(product);
    }

    @Test
    void create_sameUserTwice_savesTwoReviews() {
        when(productService.findById(1L)).thenReturn(product);
        when(reviewRepository.averageRating(1L)).thenReturn(4.0);
        when(reviewRepository.countByProductId(1L)).thenReturn(2L);

        reviewService.create(user, 1L, new ReviewRequest(5, null));
        reviewService.create(user, 1L, new ReviewRequest(3, null));

        verify(reviewRepository, times(2)).save(any(Review.class));
    }

    @Test
    void create_unknownProduct_throwsNotFound() {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Producto no encontrado con id: 99"));

        assertThrows(ResourceNotFoundException.class, () -> reviewService.create(user, 99L, new ReviewRequest(4, null)));
        verify(reviewRepository, never()).save(any(Review.class));
    }
}
