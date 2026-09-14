package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAll_shouldReturnAllCategories() {

        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setCategory("Hotel");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setCategory("Glamping");

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<Category> result = categoryService.findAll();

        assertEquals(2, result.size());
        assertEquals("Hotel", result.get(0).getCategory());

    }
}