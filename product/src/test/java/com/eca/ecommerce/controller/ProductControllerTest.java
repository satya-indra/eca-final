package com.eca.ecommerce.controller;

import com.eca.ecommerce.dto.request.ProductPurchaseRequest;
import com.eca.ecommerce.dto.request.ProductRequest;
import com.eca.ecommerce.dto.response.ProductPurchaseResponse;
import com.eca.ecommerce.dto.response.ProductResponse;
import com.eca.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private List<ProductPurchaseRequest> purchaseRequests;
    private List<ProductPurchaseResponse> purchaseResponses;

    @BeforeEach
    public void setUp() {
        productRequest = mock(ProductRequest.class);
        // Initialize productRequest fields

        productResponse = mock(ProductResponse.class);;
        // Initialize productResponse fields

        purchaseRequests = Collections.singletonList(mock(ProductPurchaseRequest.class));
        // Initialize purchaseRequests fields

        purchaseResponses = Collections.singletonList(mock(ProductPurchaseResponse.class));
        // Initialize purchaseResponses fields
    }

    @Test
    public void testCreateProduct() throws Exception {
        Mockito.when(productService.createProduct(any(ProductRequest.class), any(String.class)))
                .thenReturn(1);
        productRequest = new ProductRequest(1,"abc","abc",1.0,new BigDecimal(12.21),2,1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "test-user")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void testPurchaseProducts() throws Exception {
        Mockito.when(productService.purchaseProducts(any(List.class)))
                .thenReturn(purchaseResponses);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequests)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(purchaseResponses)));
    }

    @Test
    public void testFindById() throws Exception {
        Mockito.when(productService.findById(anyInt()))
                .thenReturn(productResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productResponse)));
    }

    @Test
    public void testFindAll() throws Exception {
        Page<ProductResponse> productPage = new PageImpl<>(Collections.singletonList(productResponse));
        Mockito.when(productService.findAll(any(String.class), any(PageRequest.class)))
                .thenReturn(productPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/findAll")
                        .param("category", "electronics")
                        .param("page", "0")
                        .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
