package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.controller.dto.ProductRequest;
import com.jc.professional_challenge_api.entities.Address;
import com.jc.professional_challenge_api.entities.Category;
import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.entities.Feature;
import com.jc.professional_challenge_api.entities.Product;
import com.jc.professional_challenge_api.entities.ProductImage;
import com.jc.professional_challenge_api.repository.CategoryRepository;
import com.jc.professional_challenge_api.repository.CityRepository;
import com.jc.professional_challenge_api.repository.FeatureRepository;
import com.jc.professional_challenge_api.repository.ProductRepository;
import com.jc.professional_challenge_api.repository.UserRepository;
import com.jc.professional_challenge_api.exception.DuplicateResourceException;
import com.jc.professional_challenge_api.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    //Folder where uploaded images are stored and the URL prefix they are served from (see WebConfig).
    private static final String UPLOAD_DIR = "uploads/";
    private static final String UPLOAD_URL_PREFIX = "/uploads/";

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private CityRepository cityRepository;
    private FeatureRepository featureRepository;
    private UserRepository userRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, CityRepository cityRepository,
                          FeatureRepository featureRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cityRepository = cityRepository;
        this.featureRepository = featureRepository;
        this.userRepository = userRepository;
    }

    //The client sends the feature ids, e.g. [1, 3]; this loads the real ones from the database.
    private Set<Feature> resolveFeatures(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();

        List<Feature> found = featureRepository.findAllById(ids);

        if (found.size() != ids.size()) {
            throw new ResourceNotFoundException("Una o más características no existen. ");
        }
        return new HashSet<>(found);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada. "));
    }

    private City findCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada. "));
    }

    //create product
    @Transactional
    public Product create(ProductRequest request){
        String name = request.name().trim();

        //check if the name already exists
        if (productRepository.existsByNameIgnoreCase(name)){
            throw new DuplicateResourceException("Ya existe un producto con el nombre: " + name);
        }

        Address address = new Address();
        address.setDirection(request.address().trim());

        Product product = new Product();
        product.setName(name);
        product.setDescription(request.description());
        product.setCategory(findCategory(request.categoryId()));
        product.setCity(findCity(request.cityId()));
        product.setAddress(address);
        product.setFeatures(resolveFeatures(request.featureIds()));

        return productRepository.save(product);
    }

    //Find Product by ID
    public  Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    //Update product
    @Transactional
    public Product update(Long id, ProductRequest request){

        Product existing = findById(id);

        String name = request.name().trim();
        //the new name can't belong to another product
        if (productRepository.existsByNameIgnoreCaseAndIdNot(name, id)){
            throw new DuplicateResourceException("Ya existe un producto con el nombre: " + name);
        }

        existing.setName(name);
        existing.setDescription(request.description());
        existing.setCategory(findCategory(request.categoryId()));
        existing.setCity(findCity(request.cityId()));

        //older products may have no address yet: create it instead of throwing a NullPointerException
        if (existing.getAddress() == null) {
            existing.setAddress(new Address());
        }
        existing.getAddress().setDirection(request.address().trim());

        // The edit form sends the full list of selected features: it replaces the previous ones.
        existing.getFeatures().clear();
        existing.getFeatures().addAll(resolveFeatures(request.featureIds()));

        return productRepository.save(existing);
    }

    //Delete Product
    @Transactional
    public void delete(Long id){
        Product product = findById(id);
        List<String> imageUrls = product.getImages().stream().map(ProductImage::getUrl).toList();

        //the user_favorites rows point to the product: remove it from every user's favorites first
        userRepository.findByFavorites_Id(id).forEach(user -> user.getFavorites().remove(product));

        productRepository.delete(product);

        //the database rows go with the product (cascade); the files on disk are removed here
        imageUrls.forEach(this::deleteStoredFile);
    }

    //List Product
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    // ------------------ Section Search -----------------------------

    private static final int MAX_SUGGESTIONS = 8;
    private static final int MIN_SUGGESTION_LENGTH = 2;
    private static final int NO_MATCH = Integer.MAX_VALUE;

    //Keyword search ignoring case and accents ("bogota" finds "Bogotá").
    //Results ordered by where the text matches: name, city, category, description; no match is left out.
    public List<Product> search(String text, LocalDate checkIn, LocalDate checkOut){
        validateDates(checkIn, checkOut);

        // TODO: when bookings exist, exclude products that are not available between checkIn and checkOut.
        String term = normalize(text);
        if (term.isEmpty()) return productRepository.findAll();

        return productRepository.findAll().stream()
                .filter(product -> relevance(product, term) < NO_MATCH)
                .sorted(Comparator.comparingInt(product -> relevance(product, term)))
                .toList();
    }

    //Short list of city, category and product names containing the text, for autocomplete.
    public List<String> suggestions(String text){
        String term = normalize(text);
        if (term.length() < MIN_SUGGESTION_LENGTH) return List.of();

        Set<String> result = new LinkedHashSet<>();
        cityRepository.findAll().stream().map(City::getCity)
                .filter(name -> matches(name, term)).forEach(result::add);
        categoryRepository.findAll().stream().map(Category::getCategory)
                .filter(name -> matches(name, term)).forEach(result::add);
        productRepository.findAll().stream().map(Product::getName)
                .filter(name -> matches(name, term)).forEach(result::add);

        return result.stream().limit(MAX_SUGGESTIONS).toList();
    }

    //Both dates or none, check-in not in the past, check-out after check-in (answered with 400).
    private void validateDates(LocalDate checkIn, LocalDate checkOut){
        if (checkIn == null && checkOut == null) return;
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Debes indicar la fecha de llegada y la de salida.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de llegada no puede ser anterior a hoy.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de llegada.");
        }
    }

    //Lower is more relevant: 0 name, 1 city, 2 category, 3 description, NO_MATCH otherwise.
    private int relevance(Product product, String term){
        if (matches(product.getName(), term)) return 0;
        if (product.getCity() != null && matches(product.getCity().getCity(), term)) return 1;
        if (product.getCategory() != null && matches(product.getCategory().getCategory(), term)) return 2;
        if (matches(product.getDescription(), term)) return 3;
        return NO_MATCH;
    }

    //term must already be normalized. It must match at the start of a word: "cali" finds "Cali" or "calidad",
    //but not "localizar"; "santa m" finds "Santa Marta".
    private boolean matches(String value, String term){
        if (value == null) return false;
        return Pattern.compile("(^|[^a-z0-9])" + Pattern.quote(term)).matcher(normalize(value)).find();
    }

    //Trimmed, lower case and without accents: "  Bogotá " -> "bogota".
    private String normalize(String value){
        if (value == null) return "";
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    // ------------------ Section Images -----------------------------

    //Method for saving the file and creating the ProductImage
    @Transactional
    public Product uploadImage(Long productId, MultipartFile file) throws IOException {

        Product product = findById(productId);

        String uploadDir = UPLOAD_DIR;
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

        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada en este producto"));

        product.getImages().remove(image);
        Product saved = productRepository.save(product);

        deleteStoredFile(image.getUrl());
        return saved;

    }

    public Product UpdateImageOrder(Long productId, Long imageId,  Integer newOrder){
        Product product = findById(productId);

        ProductImage image = product.getImages().stream().
                filter(img -> img.getId().equals(imageId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada en este producto. "));

        image.setDisplayOrder(newOrder);

        return productRepository.save(product);

    }


    //Deletes the file of an image stored in uploads/. External URLs (added with POST /{id}/image) are skipped.
    //A failure is only logged: the image is already gone from the database and the request must not fail for it.
    private void deleteStoredFile(String url) {
        if (url == null || !url.contains(UPLOAD_URL_PREFIX)) return;

        String fileName = url.substring(url.lastIndexOf(UPLOAD_URL_PREFIX) + UPLOAD_URL_PREFIX.length());
        Path uploadDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        Path file = uploadDir.resolve(fileName).normalize();

        //never delete anything outside the uploads folder
        if (!file.startsWith(uploadDir)) return;

        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo {}", file, e);
        }
    }
}
