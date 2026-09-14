package com.jc.professional_challenge_api.controller;


import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.repository.CategoryRepository;
import com.jc.professional_challenge_api.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ENDPOINTS
    @GetMapping
    public List<Category> getAll(){
        return categoryService.findAll();
    }
}
