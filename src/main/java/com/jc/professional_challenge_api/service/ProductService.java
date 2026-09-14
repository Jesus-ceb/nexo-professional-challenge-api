package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.entities.ProductImage;
import com.jc.professional_challenge_api.repository.CategoryRepository;
import com.jc.professional_challenge_api.repository.CityRepository;
import com.jc.professional_challenge_api.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private CityRepository cityRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, CityRepository cityRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
    }

    //create product
    @Transactional
    public Product create(Product product){
        product.setId(null);

        //check if the name already exists
        if (productRepository.existsByNameIgnoreCase(product.getName())){
            throw new IllegalStateException("Ya existe un producto con el nombre: " + product.getName());
        }

        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada. "));

        City city = cityRepository.findById(product.getCity().getId())
                .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada. "));

        product.setCategory(category);
        product.setCity(city);

        return productRepository.save(product);
    }

    //Find Product by ID
    public  Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }

    //Update product
    public Product update(Long id, Product changes){

        Product existing = findById(id);

        Category category = categoryRepository.findById(changes.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada. "));

        City city = cityRepository.findById(changes.getCity().getId())
                .orElseThrow(() -> new EntityNotFoundException("Ciudad no encontrada. "));

        existing.setName(changes.getName());
        existing.setDescription(changes.getDescription());
        existing.setCategory(category);
        existing.setCity(city);
        existing.getAddress().setDirection(changes.getAddress().getDirection());

        return productRepository.save(existing);
    }

    //Delete Product
    @Transactional
    public void delete(Long id){
        if (!productRepository.existsById(id)){
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }

    //List Product
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    // ------------------ Section Images -----------------------------

    //Method for saving the file and creating the ProductImage
    @Transactional
    public Product uploadImage(Long productId, MultipartFile file) throws IOException {

        Product product = findById(productId);

        String uploadDir = "uploads/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        ProductImage image = new ProductImage();
        image.setUrl("http://localhost:8080/uploads/" + fileName);
        image.setDisplayOrder(product.getImages().size() + 1);
        image.setProduct(product);

        product.getImages().add(image);

        return productRepository.save(product);

    }

    @Transactional
    public Product addImage(Long productId, String url, Integer displayOrder){
        Product product = findById(productId);

        ProductImage image = new ProductImage();
        image.setUrl(url);
        image.setDisplayOrder(displayOrder);
        image.setProduct(product);

        product.getImages().add(image);

        return productRepository.save(product);

    }

    public Product removeImage(Long productId, Long imageId){

        Product product = findById(productId);

        boolean removed = product.getImages().removeIf(img -> img.getId().equals(imageId));

        if (!removed){
            throw new EntityNotFoundException("Imagen no encontrada en este producto");
        }

        return productRepository.save(product);

    }

    public Product UpdateImageOrder(Long productId, Long imageId,  Integer newOrder){
        Product product = findById(productId);

        ProductImage image = product.getImages().stream().
                filter(img -> img.getId().equals(imageId))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Imagen no encontrada en este producto. "));

        image.setDisplayOrder(newOrder);

        return productRepository.save(product);

    }







}
