package com.jc.professional_challenge_api.controller;


import com.jc.professional_challenge_api.controller.dto.ProductImageRequest;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ENDPOINTS

    //create product
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product){

        try {
            Product product1 = productService.create(product);

            return ResponseEntity.status(HttpStatus.CREATED).body(product1);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    //Find product by id
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id){
        return productService.findById(id);
    }

    //update product
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id,@RequestBody Product changes){
        return productService.update(id, changes);
    }

    //Delete product by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //List products
    @GetMapping
    public List<Product> getAll(){
        return productService.findAll();
    }

    //------------------ ENDPOINTS image specific ------------------

    //Add image
    @PostMapping("/{productId}/image")
    public Product addImage(@PathVariable Long productId,@RequestBody ProductImageRequest request){
        return productService.addImage(productId, request.url(), request.displayOrder());
    }

    //delete image
    @DeleteMapping("/{productId}/images/{imageId}")
    public Product removeImage(@PathVariable Long productId,@PathVariable Long imageId){
        return productService.removeImage(productId, imageId);
    }

    //Order images
    @PatchMapping("/{productId}/images/{imageId}/order")
    public Product updateImageOrder(@PathVariable Long productId,@PathVariable Long imageId,@RequestBody Integer newOrder){
        return productService.UpdateImageOrder(productId, imageId, newOrder);
    }

    @PostMapping("/{productId}/images/upload")
    public Product uploadImage(@PathVariable Long productId, @RequestParam("file")MultipartFile file) throws IOException{
        return productService.uploadImage(productId, file);
    }



}
