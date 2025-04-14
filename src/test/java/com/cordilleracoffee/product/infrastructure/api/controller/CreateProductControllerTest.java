package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class CreateProductControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UploadImageService uploadImageService;


    @Test
    void shouldCreateProduct() throws Exception {


        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataFactory.validCreateProductRequestString()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnErrorWhenProductNameIsNotProvided() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.remove("name");

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenProductNameIsEmpty() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("name", "");

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenProductDescriptionIsEmpty() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("description", "");

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenProductCategoryIsNotProvided() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.remove("category");

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenThereIsNoImages() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("images", Collections.emptyList());

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenPriceIsNegative() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("basePrice", Map.of("amount", -1, "currency", "USD"));

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }
}
