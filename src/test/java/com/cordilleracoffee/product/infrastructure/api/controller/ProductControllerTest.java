package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UploadImageService uploadImageService;


    @Test
    void shouldReturnUploadUrls() throws Exception {

        when(uploadImageService.getSignedUrls(any())).thenReturn(List.of(
                new SignedUrl("25", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test1.jpg"),
                new SignedUrl("26", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test2.jpg")
        ));

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


    @Test
    void shouldReturnErrorWhenNoFilesProvided() throws Exception {

        mockMvc.perform(post("/v1/products/images/upload-urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                   "files":[]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("files: At least one file is required"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"xml", "csv"})
    void shouldReturnErrorWhenImageNameDoesNotEndWithValidExtension(String extension) throws Exception {

        mockMvc.perform(post("/v1/products/images/upload-urls")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"files\":[ {\"imageName\":\"test1." + extension + "\"}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("files[0].imageName: Incorrect file type. Only .jpg and .png are allowed"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"jpg", "png"})
    void shouldAnswerImageNameEndWithValidExtension(String extension) throws Exception {

        when(uploadImageService.getSignedUrls(any())).thenReturn(List.of(
                new SignedUrl("25", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test1.jpg")
        ));

        mockMvc.perform(post("/v1/products/images/upload-urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"files\":[ {\"imageName\":\"test1." + extension + "\"}]}"))
                .andExpect(status().isCreated());
    }
}
