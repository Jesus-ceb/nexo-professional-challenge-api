package com.jc.professional_challenge_api.controller;


import com.jc.professional_challenge_api.controller.dto.ProductImageRequest;
import com.jc.professional_challenge_api.controller.dto.ProductRequest;
import com.jc.professional_challenge_api.controller.dto.ProductResponse;
import com.jc.professional_challenge_api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// Receives ProductRequest and returns ProductResponse, never the JPA entity.
// Errors (404, 409, 400) are answered by GlobalExceptionHandler.
@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ENDPOINTS

    //create product, 409 if the name already exists
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(productService.create(request)));
    }

    //Find product by id
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id){
        return ProductResponse.from(productService.findById(id));
    }

    //update product, 409 if the new name belongs to another product
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request){
        return ProductResponse.from(productService.update(id, request));
    }

    //Delete product by id (also deletes its image files)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //List products
    @GetMapping
    public List<ProductResponse> getAll(){
        return productService.findAll().stream().map(ProductResponse::from).toList();
    }

    //------------------ ENDPOINTS image specific ------------------

    //Add image
    @PostMapping("/{productId}/image")
    public ProductResponse addImage(@PathVariable Long productId,@RequestBody ProductImageRequest request){
        return ProductResponse.from(productService.addImage(productId, request.url(), request.displayOrder()));
    }

    //delete image (also deletes the file from uploads/)
    @DeleteMapping("/{productId}/images/{imageId}")
    public ProductResponse removeImage(@PathVariable Long productId,@PathVariable Long imageId){
        return ProductResponse.from(productService.removeImage(productId, imageId));
    }

    //Order images
    @PatchMapping("/{productId}/images/{imageId}/order")
    public ProductResponse updateImageOrder(@PathVariable Long productId,@PathVariable Long imageId,@RequestBody Integer newOrder){
        return ProductResponse.from(productService.UpdateImageOrder(productId, imageId, newOrder));
    }

    @PostMapping("/{productId}/images/upload")
    public ProductResponse uploadImage(@PathVariable Long productId, @RequestParam("file")MultipartFile file) throws IOException{
        return ProductResponse.from(productService.uploadImage(productId, file));
    }



}
