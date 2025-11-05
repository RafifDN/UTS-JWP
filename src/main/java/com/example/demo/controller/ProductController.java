package com.example.demo.controller;

import com.example.demo.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final List<Product> productList = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    // GET
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productList);
    }

    // POST
    @PostMapping
    public ResponseEntity<?> addProduct (@RequestBody Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty() ||
            product.getCategory() == null || product.getCategory().trim().isEmpty() ||
            product.getPrice() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("All fields (name, category, price) must be filled!");
        }

        int newId = idCounter.incrementAndGet();
        product.setId(newId);
        productList.add(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // GET
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        Optional<Product> found = productList.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product updatedProduct) {
        Optional<Product> foundProduct = productList.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (foundProduct.isPresent()) {
            Product existingProduct = foundProduct.get();
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setPrice(updatedProduct.getPrice());
            return ResponseEntity.ok(existingProduct);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        boolean removed = productList.removeIf(p -> p.getId() == id);
        if (removed) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
}
