package com.jc.professional_challenge_api.controller.dto;

import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.entities.Feature;
import com.jc.professional_challenge_api.entities.Product;

import java.util.List;
import java.util.Set;

// What the API returns for a product. Keeps the same JSON shape the frontend already reads
// (category.category, city.city, address.direction, images[].url, features[].icon) without exposing the JPA entity.
public record ProductResponse(
        Long id,
        String name,
        String description,
        Category category,
        City city,
        AddressResponse address,
        List<ImageResponse> images,
        Set<Feature> features
) {

    public record AddressResponse(Long id, String direction) {}

    public record ImageResponse(Long id, String url, Integer displayOrder) {}

    public static ProductResponse from(Product product) {
        AddressResponse address = product.getAddress() == null ? null
                : new AddressResponse(product.getAddress().getId(), product.getAddress().getDirection());

        List<ImageResponse> images = product.getImages().stream()
                .map(image -> new ImageResponse(image.getId(), image.getUrl(), image.getDisplayOrder()))
                .toList();

        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getCategory(), product.getCity(), address, images, product.getFeatures());
    }
}
