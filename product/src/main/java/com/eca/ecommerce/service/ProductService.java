package com.eca.ecommerce.service;

import com.eca.ecommerce.controller.ProductPurchaseRequest;
import com.eca.ecommerce.controller.ProductPurchaseResponse;
import com.eca.ecommerce.controller.ProductRequest;
import com.eca.ecommerce.controller.ProductResponse;
import com.eca.ecommerce.exception.ProductPurchaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    Integer createProduct(ProductRequest request, String userId);

    ProductResponse findById(Integer id);

    Page<ProductResponse> findAll(String category, Pageable pageable);

    @Transactional(rollbackFor = ProductPurchaseException.class)
    List<ProductPurchaseResponse> purchaseProducts(
            List<ProductPurchaseRequest> request
    );

}
