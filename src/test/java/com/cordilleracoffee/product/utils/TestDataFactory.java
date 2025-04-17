package com.cordilleracoffee.product.utils;


import com.cordilleracoffee.product.domain.model.*;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class TestDataFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestDataFactory() {
    }


    public static String validCreateProductRequestString() {
        return """
                {
                  "name": "Grinded coffee beans",
                  "description": "Grinded coffe beans in a black package with the image of the farm",
                  "category": {
                    "id": 20
                  },
                  "sku": "CAF-MR",
                  "stock": 55,
                  "status": "AVAILABLE",
                  "basePrice": {
                    "amount": 30.5,
                    "currency": "USD"
                  },
                  "images": [
                    {
                      "id": "b08ee4b0-22bb-446c-bd33-0343a77e9b11",
                      "isPrimary": true,
                      "displayOrder": 2
                    }
                  ],
                  "variants": [
                    {
                      "name": "package of 250gr",
                      "description": "Small package",
                      "stock": 55,
                      "basePrice": {
                        "amount": 30.5,
                        "currency": "USD"
                      },
                      "isPrimary": true,
                      "sku": "CAF-2500-MR",
                      "images": [
                          {
                            "id": "5f52676b-2056-4da5-9d23-5f78c88409f2",
                            "isPrimary": true,
                            "displayOrder": 2
                          }
                        ]
                    }
                  ],
                  "tags": [
                    {
                      "id": 20
                    }
                  ]
                }
                
                """;
    }

    public static CreateProductRequest validCreateProductRequest() {
        try {
            return objectMapper.readValue(validCreateProductRequestString(), CreateProductRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Map<String, Object> validCreateProductRequestMap() {
        try {
            TypeReference<Map<String, Object>> reference = new TypeReference<Map<String, Object>>() {
            };

            return objectMapper.readValue(validCreateProductRequestString(), reference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Product validProduct() {

        return new Product.Builder("product1", "desc", "user-123", new Sku("SKU"), 1L,
                Set.of(new ProductImage(1L, "image", 1, true, "url.com")))
                .basePrice(new Money(BigDecimal.TEN, "EUR"))
                .stock(new Stock(10L))
                .status(ProductStatus.AVAILABLE)
                .build();

    }


    public static @NotNull Variant validProductVariant() {
        return new Variant("variant1", "desc1",
                new Stock(10L), new Money(BigDecimal.TEN, "EUR"), true,
                new Sku("SKU123"), Set.of(new VariantImage(1L, "image", 1, true, "url.com")));
    }
}
