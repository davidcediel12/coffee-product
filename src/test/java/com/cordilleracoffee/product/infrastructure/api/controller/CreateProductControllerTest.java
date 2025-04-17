package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.CreateProductService;
import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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

    @MockitoBean
    CreateProductService createProductService;


    @Test
    void shouldCreateProduct() throws Exception {


        mockMvc.perform(createProductPost()
                        .content(TestDataFactory.validCreateProductRequestString()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnErrorWhenProductNameIsNotProvided() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.remove("name");

        mockMvc.perform(createProductPost()
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenProductNameIsEmpty() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("name", "");

        mockMvc.perform(createProductPost()
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenProductDescriptionIsEmpty() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("description", "");

        mockMvc.perform(createProductPost()
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenProductCategoryIsNotProvided() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.remove("category");

        mockMvc.perform(createProductPost()
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenThereIsNoImages() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("images", Collections.emptyList());

        mockMvc.perform(createProductPost()
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenPriceIsNegative() throws Exception {

        var productRequest = TestDataFactory.validCreateProductRequestMap();
        productRequest.put("basePrice", Map.of("amount", -1, "currency", "USD"));

        mockMvc.perform(createProductPost()
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }

    private static @NotNull MockHttpServletRequestBuilder createProductPost() {
        return post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("App-User-ID", "user-123")
                .header("App-User-Roles", "SELLER");
    }
}
