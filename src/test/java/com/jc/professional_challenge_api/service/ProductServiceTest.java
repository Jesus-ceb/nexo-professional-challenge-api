package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.ProductRequest;
import com.jc.professional_challenge_api.entities.Address;
import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.entities.ProductImage;
import com.jc.professional_challenge_api.exception.DuplicateResourceException;
import com.jc.professional_challenge_api.repository.CategoryRepository;
import com.jc.professional_challenge_api.repository.CityRepository;
import com.jc.professional_challenge_api.repository.ProductRepository;
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
    private ProductRequest request;

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

        // Body that the client sends to create / update
        request = new ProductRequest("Hotel Marriot", "Hotel en el centro", 1L, 1L, "Carrera 10 #20-30", null);

    }

    @Test
    void create_YouShouldSaveTheProductWhenBothTheCatAndCitExist() {

        // Arrenge: We configure what each mock should respond to.
        when(productRepository.existsByNameIgnoreCase("Hotel Marriot")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(productRepository.save(any(Product.class))).thenReturn(product);


        // Act: ejecutamos el metodo real que queremos probar
        Product result = productService.create(request);

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
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> productService.create(request)
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
        ProductRequest changes = new ProductRequest("Hotel Marriot Renovado", "Nueva descripcion", 1L, 1L, "Nueva direccion", null);

        // The "existing" product also needs an Address to avoid throwing a NullPointerException
        Address existingAddress = new Address();
        existingAddress.setDirection("Direccion vieja");
        product.setAddress(existingAddress);

        // Act
        Product result = productService.update(1L, changes);

        // Assert
        assertNotNull(result);
        assertEquals("Hotel Marriot Renovado", product.getName());
        assertEquals("Nueva direccion", product.getAddress().getDirection());
        verify(productRepository, times(1)).save(any(Product.class));


    }

    @Test
    void update_shouldThrowExceptionIfTheNameBelongsToAnotherProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByNameIgnoreCaseAndIdNot("Hotel Hilton", 1L)).thenReturn(true);

        ProductRequest changes = new ProductRequest("Hotel Hilton", "Descripcion", 1L, 1L, "Direccion", null);

        // Act + Assert
        assertThrows(DuplicateResourceException.class, () -> productService.update(1L, changes));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_shouldCreateTheAddressWhenTheProductHasNone() {
        // Arrange: existing product without address (used to throw NullPointerException)
        product.setAddress(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.update(1L, request);

        // Assert
        assertNotNull(product.getAddress());
        assertEquals("Carrera 10 #20-30", product.getAddress().getDirection());
    }

    @Test
    void delete_shouldEliminateWhenItExists() {

        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act + Assert
        productService.delete(1L);

        verify(productRepository, times(1)).delete(product);
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

    @Test
    void removeImage_shouldDeleteTheStoredFile() throws Exception {
        // Arrange: a real file inside uploads/ linked to the product
        java.nio.file.Path uploads = java.nio.file.Paths.get("uploads");
        java.nio.file.Files.createDirectories(uploads);
        java.nio.file.Path file = uploads.resolve("test_remove_image.jpg");
        java.nio.file.Files.writeString(file, "img");

        ProductImage image = new ProductImage();
        image.setId(5L);
        image.setUrl("http://localhost:8080/uploads/test_remove_image.jpg");
        product.setImages(new ArrayList<>(List.of(image)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.removeImage(1L, 5L);

        // Assert
        assertTrue(product.getImages().isEmpty());
        assertFalse(java.nio.file.Files.exists(file));
    }

}