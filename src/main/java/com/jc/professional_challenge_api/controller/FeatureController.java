package com.jc.professional_challenge_api.controller;

import com.jc.professional_challenge_api.controller.dto.FeatureRequest;
import com.jc.professional_challenge_api.entities.Feature;
import com.jc.professional_challenge_api.service.FeatureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// GET is public (the site shows the features); POST/PUT/DELETE are admin only (see SecurityConfig).
@RestController
@RequestMapping("features")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    //List features
    @GetMapping
    public List<Feature> getAll() {
        return featureService.findAll();
    }

    //Create feature, 409 if the name already exists
    @PostMapping
    public ResponseEntity<Feature> create(@Valid @RequestBody FeatureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(featureService.create(request));
    }

    //Update feature, 409 if the new name belongs to another feature
    @PutMapping("/{id}")
    public Feature update(@PathVariable Long id, @Valid @RequestBody FeatureRequest request) {
        return featureService.update(id, request);
    }

    //Delete feature (it is also removed from the products that had it)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        featureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
