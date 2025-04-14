package com.cordilleracoffee.product.e2e;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.cordilleracoffee.product.infrastructure.dto.ApiErrorResponse;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.SignedUrl;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import com.redis.testcontainers.RedisContainer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UploadImageE2ETest {

    private static final String SELLER_ROLE = "SELLER";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Container
    private static final GenericContainer<?> AZURITE_CONTAINER = new GenericContainer<>(
            DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:latest"))
            .withExposedPorts(10000, 10001, 10002);

    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7-alpine"));


    @DynamicPropertySource
    static void setAzureStorageProperties(DynamicPropertyRegistry registry) {

        String connectionString = String.format("DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;" +
                "AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;" +
                "BlobEndpoint=http://127.0.0.1:%d/devstoreaccount1;", AZURITE_CONTAINER.getMappedPort(10000));

        registry.add("spring.cloud.azure.storage.blob.connection-string", () -> connectionString);

        registry.add("spring.data.redis.host", redis::getRedisHost);
        registry.add("spring.data.redis.port", redis::getRedisPort);
    }


    @BeforeEach
    void cleanUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("temp");
        containerClient.listBlobs()
                .forEach(blobItem -> containerClient.getBlobClient(blobItem.getName()).delete());
    }

    @Test
    void shouldNotSaveImageWhenUserIsNotSeller() {

        ImageUrlRequests imageUrlRequests = new ImageUrlRequests(
                List.of(
                        new ImageUrlRequest("file1.png")
                )
        );

        ResponseEntity<ApiErrorResponse> response = callUploadUrlsError(imageUrlRequests, "CUSTOMER", "user123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(redisTemplate.keys("temporalImages:*")).isEmpty();
        assertTempStorageContainerIsEmpty();
    }

    private void assertTempStorageContainerIsEmpty() {
        assertThat(blobServiceClient.getBlobContainerClient("temp").listBlobs()).isEmpty();
    }

    @Test
    void shouldReturnValidSignedUrl() throws IOException {

        String userId = "user-123";

        ImageUrlRequests imageUrlRequests = new ImageUrlRequests(
                List.of(
                        new ImageUrlRequest("file1.png")
                )
        );


        ResponseEntity<List<SignedUrl>> response = callUploadUrls(imageUrlRequests, SELLER_ROLE, userId);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<SignedUrl> signedUrls = response.getBody();

        assertThat(signedUrls).hasSize(1);

        SignedUrl signedUrl = signedUrls.getFirst();

        assertDoesNotThrow(() -> UUID.fromString(signedUrl.id()));
        assertThat(signedUrl.url()).isNotBlank();

        verifyImageUpload(signedUrl);
        verifyImageInformationInCache(signedUrl, userId);


    }

    private void verifyImageInformationInCache(SignedUrl signedUrl, String userId) {

        Map<Object, Object> entries = redisTemplate.opsForHash().entries("temporalImages:" + userId);

        assertThat(entries).hasSize(1);

        TempImage entry = (TempImage) entries.values().stream().findFirst().orElseThrow();


        assertThat(entry.id()).isEqualTo(signedUrl.id());
        assertThat(entry.userId()).isEqualTo(userId);
        assertThat(entry.url()).isEqualTo(signedUrl.url());
    }


    private void verifyImageUpload(SignedUrl signedUrl) throws IOException {

        byte[] originalImage = new ClassPathResource("/assets/testImage.png").getContentAsByteArray();

        var azureResponse = uploadFileToAzure(signedUrl.url(), originalImage);
        assertThat(azureResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var containerClient = blobServiceClient.getBlobContainerClient("temp");

        assertThat(containerClient.listBlobs().stream().count()).isEqualTo(1);

        BlobItem image = containerClient.listBlobs().stream().findFirst().get();

        BlobClient blobClient = containerClient.getBlobClient(image.getName());

        byte[] downloadedImage = blobClient.downloadContent().toBytes();
        assertThat(downloadedImage).isEqualTo(originalImage);
    }

    private ResponseEntity<List<SignedUrl>> callUploadUrls(ImageUrlRequests imageUrlRequests,
                                                           String userRole,
                                                           String userId) {

        RequestEntity<ImageUrlRequests> request = createUploadRequest(imageUrlRequests, userRole, userId);

        var typeRef = new ParameterizedTypeReference<List<SignedUrl>>() {
        };
        return testRestTemplate.exchange(request, typeRef);
    }


    private ResponseEntity<ApiErrorResponse> callUploadUrlsError(ImageUrlRequests imageUrlRequests,
                                                                 String userRole,
                                                                 String userId) {
        RequestEntity<ImageUrlRequests> request = createUploadRequest(imageUrlRequests, userRole, userId);

        return testRestTemplate.exchange(request, ApiErrorResponse.class);
    }

    private static @NotNull RequestEntity<ImageUrlRequests> createUploadRequest(ImageUrlRequests imageUrlRequests, String userRole, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("App-User-Roles", userRole);
        headers.add("App-User-ID", userId);


        return RequestEntity
                .post(URI.create("/v1/products/images/upload-urls"))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(imageUrlRequests);
    }


    private static @NotNull ResponseEntity<Void> uploadFileToAzure(String url, byte[] originalImage) {

        RestTemplate restTemplate = new RestTemplateBuilder().build();


        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("x-ms-blob-type", "BlockBlob");


        HttpEntity<byte[]> request = new HttpEntity<>(originalImage, headers);

        return restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
    }
}
