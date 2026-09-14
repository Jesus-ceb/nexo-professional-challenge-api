package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.entities.Address;
import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.repository.CategoryRepository;
import com.jc.professional_challenge_api.repository.CityRepository;
import com.jc.professional_challenge_api.repository.ProductRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    //import mocks
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CityRepository cityRepository;

    //creates a REAL instance of ProductService,
    @InjectMocks
    private ProductService productService;

    //Reusable test data across multiple tests
    private Product product;
    private Category category;
    private City city;

    //It is executed BEFORE each @Test, to configure everything before the test
    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setCategory("Hotel");

        city = new City();
        city.setId(1L);
        city.setCity("Cali");

        product = new Product();
        product.setId(1L);
        product.setName("Hotel Marriot");
        product.setCategory(category);
        product.setCity(city);

    }

    @Test
    void create_YouShouldSaveTheProductWhenBothTheCatAndCitExist() {

        // Arrenge: We configure what each mock should respond to.
        when(productRepository.existsByNameIgnoreCase("Hotel Marriot")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(productRepository.save(any(Product.class))).thenReturn(product);


        // Act: ejecutamos el metodo real que queremos probar
        Product result = productService.create(product);

        // Assert: We verify that the result is as expected.
        assertNotNull(result);
        assertEquals("Hotel Marriot", product.getName());

        // verify: confirms that productRepository.save() was indeed called exactly once
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void create_shouldThrowExceptionIfTheNameAlreadyExists(){
        // Arrange
        when(productRepository.existsByNameIgnoreCase("Hotel Marriot")).thenReturn(true);

        // Act + Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> productService.create(product)
        );

        assertEquals("Ya existe un producto con el nombre: Hotel Marriot", exception.getMessage());

        // Verify
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void findById_shouldReturnProductWhenItExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findById(1L);

        // Assert
        assertEquals("Hotel Marriot", result.getName());
    }

    @Test
    void update_shouldUpdateProductWhenExists() {

        // Arrange : the "existing" product in the database (internal findById mock)
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // "changes" that come from the customer
        Product changes = new Product();
        changes.setName("Hotel Marriot Renovado");
        changes.setDescription("Nueva descripcion");
        changes.setCategory(category);
        changes.setCity(city);
        Address address = new Address();
        address.setDirection("Nueva direccion");
        changes.setAddress(address);

        // The "existing" product also needs an Address to avoid throwing a NullPointerException
        Address existingAddress = new Address();
        existingAddress.setDirection("Direccion vieja");
        product.setAddress(existingAddress);

        // Act
        Product result = productService.update(1L, changes);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));


    }

    @Test
    void delete_shouldEliminateWhenItExists() {

        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act + Assert
        productService.delete(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void findAll_shouldReturnProductList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product));

        // ACt
        List<Product> result = productService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Hotel Marriot", result.get(0).getName());
    }

    @Test
    void addImage_shouldAddImageToProduct() {

        // Arrange
        product.setImages(new ArrayList<>()); // The product starts without images
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.addImage(1L, "http://example.com/foto.jpg", 1);

        // Assert
        assertEquals(1, result.getImages().size());
        assertEquals("http://example.com/foto.jpg", result.getImages().get(0).getUrl());
        verify(productRepository, times(1)).save(any(Product.class));

    }

}