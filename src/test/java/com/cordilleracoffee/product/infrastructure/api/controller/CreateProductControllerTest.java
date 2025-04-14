package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.application.UploadImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class CreateProductControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UploadImageService uploadImageService;


    @Test
    void shouldCreateProduct() throws Exception {


        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Grinded coffee beans",
                                  "description": "Grinded coffe beans in a black package with the image of the farm",
                                  "category": {
                                    "id": 20
                                  },
                                  "sku": "CAF-MR",
                                  "stock": 55,
                                  "status": "AVAILABLE",
                                  "basePrice": 30.5,
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
                                      "basePrice": 30.5,
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
                                """))
                .andExpect(status().isCreated());
    }
}
