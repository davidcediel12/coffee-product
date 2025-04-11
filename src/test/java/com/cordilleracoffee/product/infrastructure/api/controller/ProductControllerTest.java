package com.cordilleracoffee.product.infrastructure.api.controller;


import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UploadImageService uploadImageService;


    @Test
    void shouldReturnUploadUrls() throws Exception {

        when(uploadImageService.getSignedUrls(any(), anyString(), anyList())).thenReturn(List.of(
                new SignedUrl("25", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test1.jpg"),
                new SignedUrl("26", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test2.jpg")
        ));

        mockMvc.perform(uploadUrlFromSeller()
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

        mockMvc.perform(uploadUrlFromSeller()
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

        mockMvc.perform(uploadUrlFromSeller()
                        .content("{\"files\":[ {\"imageName\":\"test1." + extension + "\"}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("files[0].imageName: Incorrect file type. Only .jpg and .png are allowed"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"jpg", "png"})
    void shouldAnswerImageNameEndWithValidExtension(String extension) throws Exception {

        when(uploadImageService.getSignedUrls(any(), anyString(), anyList())).thenReturn(List.of(
                new SignedUrl("25", "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/test1.jpg")
        ));

        mockMvc.perform(uploadUrlFromSeller()
                        .content("{\"files\":[ {\"imageName\":\"test1." + extension + "\"}]}"))
                .andExpect(status().isCreated());
    }


    @Test
    void shouldRejectImageNameWithSubfolders() throws Exception {
        mockMvc.perform(uploadUrlFromSeller()
                        .content("{\"files\":[ {\"imageName\":\"../../../evil.png\"}]}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(uploadImageService);
    }

    @Test
    void shouldReturnErrorWhenUserRolesAreNotProvided() throws Exception {
        mockMvc.perform(post("/v1/products/images/upload-urls")
                        .header("App-User-ID", "user-123")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"files\":[ {\"imageName\":\"someImage.png\"}]}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnErrorWhenUserIdNotProvided() throws Exception {

        mockMvc.perform(post("/v1/products/images/upload-urls")
                        .header("App-User-Roles", "SELLER")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"files\":[ {\"imageName\":\"someImage.png\"}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request header 'App-User-ID' for method parameter type String is not present"));
    }


    @Test
    void shouldReturnErrorWhenUploadMoreThanTenImages() throws Exception {


        mockMvc.perform(uploadUrlFromSeller()
                        .content(toJsoString(new ImageUrlRequests(List.of(
                                new ImageUrlRequest("img1.png"),
                                new ImageUrlRequest("img2.png"),
                                new ImageUrlRequest("img3.png"),
                                new ImageUrlRequest("img4.png"),
                                new ImageUrlRequest("img5.png"),
                                new ImageUrlRequest("img6.png"),
                                new ImageUrlRequest("img7.png"),
                                new ImageUrlRequest("img8.png"),
                                new ImageUrlRequest("img9.png"),
                                new ImageUrlRequest("img10.png"),
                                new ImageUrlRequest("img11.png")
                        )))))
                .andExpect(status().isBadRequest());

    }


    private String toJsoString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static MockHttpServletRequestBuilder uploadUrlFromSeller() {
        return post("/v1/products/images/upload-urls")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("App-User-ID", "user-123")
                .header("App-User-Roles", "SELLER");
    }
}
