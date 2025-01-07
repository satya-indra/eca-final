package com.eca.ecommerce.service;


import com.eca.ecommerce.dto.mapper.ProductMapper;
import com.eca.ecommerce.dto.request.ProductPurchaseRequest;
import com.eca.ecommerce.dto.request.ProductRequest;
import com.eca.ecommerce.dto.response.ProductPurchaseResponse;
import com.eca.ecommerce.dto.response.ProductResponse;
import com.eca.ecommerce.entity.Product;
import com.eca.ecommerce.exception.ProductPurchaseException;
import com.eca.ecommerce.repo.ProductRepository;
import com.eca.ecommerce.service.impl.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;
    private List<ProductPurchaseRequest> purchaseRequests;
    private List<ProductPurchaseResponse> purchaseResponses;

    @BeforeEach
    public void setUp() {
        productRequest = mock(ProductRequest.class);
        // Initialize productRequest fields

        product = new Product();
        // Initialize product fields

        productResponse = mock(ProductResponse.class);
        // Initialize productResponse fields

        purchaseRequests = Collections.singletonList(mock(ProductPurchaseRequest.class));
        // Initialize purchaseRequests fields

        purchaseResponses = Collections.singletonList(mock( ProductPurchaseResponse.class));
        // Initialize purchaseResponses fields
    }

    @Test
    public void testCreateProduct() {
        when(mapper.toProduct(any(ProductRequest.class), any(String.class))).thenReturn(product);
        when(repository.save(any(Product.class))).thenReturn(product);
        productRequest = new ProductRequest(1,"abc","abc",1.0,new BigDecimal(12.21),2,1);

        Integer productId = productService.createProduct(productRequest, "test-user");

        assertEquals(productId, product.getId());
        verify(repository, times(1)).save(product);
    }

    @Test
    public void testFindById() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(product));
        when(mapper.toProductResponse(any(Product.class))).thenReturn(productResponse);

        ProductResponse response = productService.findById(1);

        assertNotNull(response);
        verify(repository, times(1)).findById(1);
    }

    @Test
    public void testFindByIdNotFound() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findById(1));
    }

    @Test
    public void testFindAll() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(repository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(mapper.toProductResponse(any(Product.class))).thenReturn(productResponse);

        Page<ProductResponse> responsePage = productService.findAll(null, PageRequest.of(0, 10));

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testPurchaseProducts() {
        when(repository.findAllByIdInOrderById(anyList())).thenReturn(Collections.singletonList(product));

        List<ProductPurchaseResponse> responses = productService.purchaseProducts(purchaseRequests);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    public void testPurchaseProductsException() {
        when(repository.findAllByIdInOrderById(anyList())).thenReturn(Collections.emptyList());

        assertThrows(ProductPurchaseException.class, () -> productService.purchaseProducts(purchaseRequests));
    }
}
