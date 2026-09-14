package com.jc.professional_challenge_api.repository;

import com.jc.professional_challenge_api.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
