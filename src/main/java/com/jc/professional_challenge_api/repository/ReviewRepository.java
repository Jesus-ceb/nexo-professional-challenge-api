package com.jc.professional_challenge_api.repository;

import com.jc.professional_challenge_api.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    //Reviews of a product, newest first.
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    //Average rating of a product (null when it has no reviews).
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double averageRating(@Param("productId") Long productId);

    long countByProductId(Long productId);
}
