package com.eca.ecommerce.service.impl;

import com.eca.ecommerce.controller.*;
import com.eca.ecommerce.exception.ProductPurchaseException;
import com.eca.ecommerce.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Integer createProduct( ProductRequest request, String userId) {
        // we also expect product images here. But will think of implement it later

        var product = mapper.toProduct(request, userId);

        // we also need to map product to given user.
        // we have two options --> get user id/name from json OR extract is from token
        // we will fetch it from token Principal

        return repository.save(product).getId();
    }

    @Override
    public ProductResponse findById(Integer id) {

        ProductResponse pr = repository.findById(id)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID:: " + id));
        log.info("getting  product result by id {}", pr);
        return  pr;
    }

    @Override
    public Page<ProductResponse> findAll(String category, Pageable pageable) {
        log.info("getting all product result from product service");
        if (category != null) {
            Page<Product> productPage = repository.findByCategory(category, pageable);
            return productPage.map(mapper::toProductResponse);
        }
        return repository.findAll(pageable).map(mapper::toProductResponse);
    }

    @Override
    @Transactional(rollbackFor = ProductPurchaseException.class)
    public List<ProductPurchaseResponse> purchaseProducts(
            List<ProductPurchaseRequest> request
    ) {
        var productIds = request
                .stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        var storedProducts = repository.findAllByIdInOrderById(productIds);
        log.info("Getting product details by id {}",storedProducts);

        if (productIds.size() != storedProducts.size()) {
            log.error("Getting product empty details {}",request);
            throw new ProductPurchaseException("One or more products does not exist");
        }
        var sortedRequest = request
                .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for (int i = 0; i < storedProducts.size(); i++) {
            var product = storedProducts.get(i);
            var productRequest = sortedRequest.get(i);
            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("Insufficient stock quantity for product with ID:: " + productRequest.productId());
            }
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);
            purchasedProducts.add(mapper.toproductPurchaseResponse(product, productRequest.quantity()));
        }
        return purchasedProducts;
    }

}
