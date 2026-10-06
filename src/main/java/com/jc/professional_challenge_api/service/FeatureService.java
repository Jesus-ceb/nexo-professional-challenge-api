package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.FeatureRequest;
import com.jc.professional_challenge_api.entities.Feature;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.repository.FeatureRepository;
import com.jc.professional_challenge_api.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final ProductRepository productRepository;

    public FeatureService(FeatureRepository featureRepository, ProductRepository productRepository) {
        this.featureRepository = featureRepository;
        this.productRepository = productRepository;
    }

    public List<Feature> findAll() {
        return featureRepository.findAll();
    }

    public Feature findById(Long id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Característica no encontrada con id: " + id));
    }

    @Transactional
    public Feature create(FeatureRequest request) {
        String name = request.name().trim();
        if (featureRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalStateException("Ya existe una característica con el nombre: " + name);
        }

        Feature feature = new Feature();
        feature.setName(name);
        feature.setIcon(request.icon());
        return featureRepository.save(feature);
    }

    @Transactional
    public Feature update(Long id, FeatureRequest request) {
        Feature feature = findById(id);

        String name = request.name().trim();
        if (featureRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalStateException("Ya existe una característica con el nombre: " + name);
        }

        feature.setName(name);
        feature.setIcon(request.icon());
        return featureRepository.save(feature);
    }

    //Removes the feature from every product that uses it first, otherwise the join table blocks the delete.
    @Transactional
    public void delete(Long id) {
        Feature feature = findById(id);

        for (Product product : productRepository.findByFeatures_Id(id)) {
            product.getFeatures().remove(feature);
        }

        featureRepository.delete(feature);
    }
}
