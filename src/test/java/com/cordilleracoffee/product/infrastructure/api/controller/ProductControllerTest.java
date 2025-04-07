package com.cordilleracoffee.product.infrastructure.api.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {


    @Autowired
    MockMvc mockMvc;


    @Test
    void shouldReturnUploadUrls() throws Exception {

        mockMvc.perform(post("/v1/products/images/upload-urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                   "files":[
                                      {
                                         "imageName":"test1.jpg"
                                      },
                                      {
                                         "imageName":"test2.jpg"
                                      }
                                   ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("25"))
                .andExpect(jsonPath("$[0].url").value("https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test1.jpg"));

    }
}
