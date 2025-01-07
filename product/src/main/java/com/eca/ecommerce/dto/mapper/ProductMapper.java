package com.eca.ecommerce.dto.mapper;

import com.eca.ecommerce.category.Category;
import com.eca.ecommerce.dto.request.ProductRequest;
import com.eca.ecommerce.dto.response.ProductPurchaseResponse;
import com.eca.ecommerce.dto.response.ProductResponse;
import com.eca.ecommerce.entity.Product;
import org.springframework.stereotype.Service;

/**
 * https://java-design-patterns.com/patterns/data-mapper/
 * The Builder Pattern in your Product POJO is a Creational Pattern.
 * The Data Mapper functionality of your mapper aligns most closely with Behavioral Patterns due to its role in coordinating data transformation behavior.
 */
@Service
public class ProductMapper {
    public Product toProduct(ProductRequest request, String userId) {
        return Product.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .availableQuantity(request.availableQuantity())
                .price(request.price())
                .category(
                        Category.builder()
                                .id(request.categoryId())
                                .build()
                )
                .userId(Integer.parseInt(userId))
                .build();
    }

    public ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAvailableQuantity(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getDescription()
        );
    }

    public ProductPurchaseResponse toproductPurchaseResponse(Product product, double quantity) {
        return new ProductPurchaseResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                quantity
        );
    }
}
