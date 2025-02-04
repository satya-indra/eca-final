package com.eca.ecommerce.controller;

import com.eca.ecommerce.dto.request.ProductPurchaseRequest;
import com.eca.ecommerce.dto.response.ProductPurchaseResponse;
import com.eca.ecommerce.dto.request.ProductRequest;
import com.eca.ecommerce.dto.response.ProductResponse;
import com.eca.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @CacheEvict(value = "products", allEntries = true)
    @PostMapping("/create")
    public ResponseEntity<Integer> createProduct(@RequestBody @Valid ProductRequest request, HttpServletRequest httpServletRequest) {
        // Extract username or userId from the request header, its passed by API Gateway, if not make sure its done
        String userId = httpServletRequest.getHeader("X-User-Id"); // Replace with the actual header key

        if (userId == null) {
            throw new IllegalArgumentException("User information is missing in the request headers");
        }

        return ResponseEntity.ok(service.createProduct(request, userId));
    }

    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchaseResponse>> purchaseProducts(@RequestBody List<ProductPurchaseRequest> request) {
        return ResponseEntity.ok(service.purchaseProducts(request));
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable("product-id") Integer productId) {
        return ResponseEntity.ok(service.findById(productId));
    }

    @Cacheable(value = "products", key = "#category + '-' + #page + '-' + #size")
    @GetMapping("/findAll")
    public Page<ProductResponse> findAll(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ) {
        Page<ProductResponse> products = service.findAll(category, PageRequest.of(page, size));
        return products;
    }


    // saga pattern
}
