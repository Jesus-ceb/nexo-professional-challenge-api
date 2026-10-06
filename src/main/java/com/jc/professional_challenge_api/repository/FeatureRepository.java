package com.jc.professional_challenge_api.repository;

import com.jc.professional_challenge_api.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    //Check for duplicate names when creating.
    boolean existsByNameIgnoreCase(String name);

    //Check for duplicate names when editing (ignoring the feature being edited).
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
