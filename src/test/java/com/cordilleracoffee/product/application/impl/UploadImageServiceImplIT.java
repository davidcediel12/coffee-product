package com.cordilleracoffee.product.application.impl;


import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.cordilleracoffee.product.application.UploadImageService;
import com.cordilleracoffee.product.config.ContainerConfig;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.SignedUrl;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class UploadImageServiceImplIT extends ContainerConfig {

    @Autowired
    UploadImageService uploadImageService;

    @Autowired
    private BlobServiceClient blobServiceClient;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @BeforeEach
    void cleanBlobContainers() {
        BlobContainerClient tempContainer = blobServiceClient.getBlobContainerClient("temp");

        tempContainer.listBlobs()
                .forEach(blobItem -> tempContainer.getBlobClient(blobItem.getName())
                        .delete());

        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();
    }

    @Test
    void shouldGenerateImage() {

        List<SignedUrl> signedUrls = uploadImageService.getSignedUrls(new ImageUrlRequests(List.of(
                new ImageUrlRequest("image1.png"))), "user-123", List.of(UserRole.SELLER));

        assertThat(signedUrls).hasSize(1);
    }


    @Test
    void generatedUrlShouldBeValidToUpload() throws IOException {

        List<SignedUrl> signedUrls = uploadImageService.getSignedUrls(new ImageUrlRequests(List.of(
                new ImageUrlRequest("image1.png"))), "user-123", List.of(UserRole.SELLER));

        assertThat(signedUrls).hasSize(1);

        String url = signedUrls.getFirst().url();
        assertThat(url).isNotBlank();

        ResponseEntity<Void> response = uploadFile(url);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        BlobItem blob = verifySingleBlobExists("temp");

        assertThat(url).contains(blob.getName());
    }


    @Test
    void shouldSaveTemporalImageDetailsInCache() {

        String userId = "user-123";
        String imageName = "image1.png";

        List<SignedUrl> signedUrls = uploadImageService.getSignedUrls(
                new ImageUrlRequests(List.of(
                        new ImageUrlRequest(imageName))), userId, List.of(UserRole.SELLER));

        assertThat(signedUrls).hasSize(1);

        String url = signedUrls.getFirst().url();
        assertThat(url).isNotBlank();

        String hashKey = "temporalImages:" + userId;

        assertThat(redisTemplate.hasKey(hashKey)).isTrue();

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);

        assertThat(entries).hasSize(1);

        Object tempImage = entries.entrySet().stream()
                .findFirst()
                .orElseThrow()
                .getValue();

        assertThat(tempImage).isInstanceOf(TempImage.class);

        TempImage image = (TempImage) tempImage;

        assertThat(image.name()).contains(imageName);
        assertThat(image.userId()).isEqualTo(userId);
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
