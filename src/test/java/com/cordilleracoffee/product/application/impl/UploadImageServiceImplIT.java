package com.cordilleracoffee.product.application.impl;


import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Testcontainers
class UploadImageServiceImplIT {

    @Container
    private static final GenericContainer<?> AZURITE_CONTAINER = new GenericContainer<>(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:latest"))
            .withExposedPorts(10000, 10001, 10002);

    @Autowired
    UploadImageService uploadImageService;


    @Test
    void shouldGenerateImage() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .build();

        List<SignedUrl> signedUrls = uploadImageService.getSignedUrls(new ImageUrlRequests(List.of(
                new ImageUrlRequest("image1.png"))));

        assertThat(signedUrls).hasSize(1);
    }
}
