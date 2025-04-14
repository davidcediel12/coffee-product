package com.cordilleracoffee.product.utils;

import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

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
                      "sku": "CAF-2500-MR"
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
}
