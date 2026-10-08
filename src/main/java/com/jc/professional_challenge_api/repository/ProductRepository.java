package com.jc.professional_challenge_api.repository;

import com.jc.professional_challenge_api.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //method to verify existence by name
    boolean existsByNameIgnoreCase(String name);

    //Same check on update: the name is taken by a product other than the one being edited.
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    //Products that have a given feature (used before deleting that feature).
    List<Product> findByFeatures_Id(Long featureId);

}
