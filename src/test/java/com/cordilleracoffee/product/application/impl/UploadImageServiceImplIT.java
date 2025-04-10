package com.cordilleracoffee.product.application.impl;


import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class UploadImageServiceImplIT {


    @Container
    private static final GenericContainer<?> AZURITE_CONTAINER = new GenericContainer<>(
            DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:latest"))
            .withExposedPorts(10000, 10001, 10002);

    @Autowired
    UploadImageService uploadImageService;

    @Autowired
    private BlobServiceClient blobServiceClient;

    @DynamicPropertySource
    static void setAzureStorageProperties(DynamicPropertyRegistry registry) {

        String connectionString = String.format("DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;" +
                "AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;" +
                "BlobEndpoint=http://127.0.0.1:%d/devstoreaccount1;", AZURITE_CONTAINER.getMappedPort(10000));

        registry.add("spring.cloud.azure.storage.blob.connection-string", () -> connectionString);
    }


    @BeforeEach
    void cleanBlobContainers() {
        BlobContainerClient tempContainer = blobServiceClient.getBlobContainerClient("temp");

        tempContainer.listBlobs()
                .forEach(blobItem -> tempContainer.getBlobClient(blobItem.getName())
                        .delete());
    }

    @Test
    void shouldGenerateImage() {

        List<SignedUrl> signedUrls = uploadImageService.getSignedUrls(new ImageUrlRequests(List.of(
                new ImageUrlRequest("image1.png"))));

        assertThat(signedUrls).hasSize(1);
    }


    @Test
    void generatedUrlShouldBeValidToUpload() throws IOException {

        List<SignedUrl> signedUrls = uploadImageService.getSignedUrls(new ImageUrlRequests(List.of(
                new ImageUrlRequest("image1.png"))));

        assertThat(signedUrls).hasSize(1);

        String url = signedUrls.getFirst().url();
        assertThat(url).isNotBlank();

        ResponseEntity<Void> response = uploadFile(url);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        BlobItem blob = verifySingleBlobExists("temp");

        assertThat(url).contains(blob.getName());
    }

    private BlobItem verifySingleBlobExists(String containerName) {
        PagedIterable<BlobItem> blobs = blobServiceClient.getBlobContainerClient(containerName)
                .listBlobs();

        assertThat(blobs.stream().count()).isEqualTo(1);

        return blobs.stream().findFirst().orElseThrow();
    }

    private static @NotNull ResponseEntity<Void> uploadFile(String url) throws IOException {

        RestTemplate restTemplate = new RestTemplateBuilder().build();

        var image = new ClassPathResource("/assets/testImage.png");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("x-ms-blob-type", "BlockBlob");


        HttpEntity<byte[]> request = new HttpEntity<>(image.getContentAsByteArray(), headers);

        return restTemplate
                .exchange(url, HttpMethod.PUT, request, Void.class);
    }


}
