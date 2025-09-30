package com.cordilleracoffee.product.e2e;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.cordilleracoffee.product.config.AzureProperties;
import com.cordilleracoffee.product.config.ContainerConfig;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.infrastructure.dto.ApiErrorResponse;
import com.cordilleracoffee.product.infrastructure.dto.saveproduct.CreateProductRequest;
import com.cordilleracoffee.product.infrastructure.persistence.BlobClientFactory;
import com.cordilleracoffee.product.infrastructure.persistence.ProductJpaRepository;
import com.cordilleracoffee.product.infrastructure.persistence.entity.Product;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import com.cordilleracoffee.product.infrastructure.persistence.impl.BlobClientFactoryImpl;
import com.cordilleracoffee.product.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CreateProductE2ETest extends ContainerConfig {

    private static final String SELLER_ROLE = "SELLER";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    AzureProperties azureProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Autowired
    private BlobClientFactory blobClientFactory;
    @Autowired
    private ProductJpaRepository productJpaRepository;


    @TestConfiguration
    static class CreateProductE2EConfig {

        @Bean
        @Primary
        BlobClientFactory blobClientFactory(BlobServiceClient blobServiceClient) {

            return Mockito.spy(new BlobClientFactoryImpl(blobServiceClient));
        }
    }


    @BeforeEach
    void cleanUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("temp");
        containerClient.listBlobs()
                .forEach(blobItem -> containerClient.getBlobClient(blobItem.getName()).delete());
    }


    @Test
    void shouldReturnErrorWhenCategoryNotExists() {

        Map<String, Object> productMap = TestDataFactory.validCreateProductRequestMap();

        ((Map<String, String>) productMap.get("category")).put("id", "100");

        var productRequest = objectMapper.convertValue(productMap, CreateProductRequest.class);


        var response = postProductError(productRequest, SELLER_ROLE, "user-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiErrorResponse errorResponse = response.getBody();

        assertThat(errorResponse)
                .isNotNull()
                .isInstanceOf(ApiErrorResponse.class);

        assertThat(errorResponse.error()).isEqualTo("PRD-VA-05");
        assertThat(errorResponse.message()).contains("Category and tag must exists to create the product");
    }



    @Test
    void shouldReturnErrorWhenProductImageIsNotInCache() {

        var response = postProductError(TestDataFactory.validCreateProductRequest(), SELLER_ROLE, "user-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ApiErrorResponse errorResponse = response.getBody();

        assertThat(errorResponse)
                .isNotNull()
                .isInstanceOf(ApiErrorResponse.class);

        assertThat(errorResponse.error()).isEqualTo("PRD-VA-05");
        assertThat(errorResponse.message()).contains("There is temporal image ids that are not present in the system");
    }


    @Test
    void shouldSaveProduct() throws IOException {

        // Prepare test environment
        saveProductImageInStorageAndCache();

        BlobContainerClient productContainer = blobServiceClient.getBlobContainerClient(azureProperties.getContainerProductAssets());

        mockFileUploadToAzurite(productContainer);

        CreateProductRequest productRequest = getProductRequestWithoutVariants();
        var response = postProduct(productRequest, UserRole.SELLER.toString(), "user-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders()).containsKey(HttpHeaders.LOCATION);

        verifyProductInDatabase(productRequest);

        BlobContainerClient tempContainer = blobServiceClient.getBlobContainerClient(azureProperties.getContainerTemp());
        assertThat(tempContainer.listBlobs()).isEmpty();

        assertThat(productContainer.listBlobs()).isNotEmpty();

    }

    private void verifyProductInDatabase(CreateProductRequest productRequest) {
        Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCT", Integer.class);
        assertThat(productCount).isEqualTo(1);

        Product product = productJpaRepository.findAll().stream().findFirst().orElseThrow();

        assertThat(product.getName()).isEqualTo(productRequest.name());
        assertThat(product.getDescription()).isEqualTo(productRequest.description());
        assertThat(product.getSku()).isEqualTo(productRequest.sku());
    }

    /**
     * All the process is not mocked, however, the upload using the method
     * uploadFromUrl(sourceUrl, overwrite) is not supported yet by azurite.
     * <p>
     * This method can be deleted when azurite support the file upload
     */
    private void mockFileUploadToAzurite(BlobContainerClient productContainer) {
        BlobContainerClient containerMock = Mockito.mock(BlobContainerClient.class);
        when(blobClientFactory.createBlobContainerClient("product-assets"))
                .thenReturn(containerMock);

        BlobClient blobMock = Mockito.mock(BlobClient.class);
        when(containerMock.getBlobClient(anyString())).thenReturn(blobMock);

        BlockBlobClient blockBlobMock = Mockito.mock(BlockBlobClient.class);
        when(blobMock.getBlockBlobClient()).thenReturn(blockBlobMock);

        doAnswer(invocation -> {
            productContainer.getBlobClient("user-123/finalProduct")
                    .upload(new ClassPathResource("/assets/testImage.png").getInputStream());
            return null;
        }).when(blockBlobMock).uploadFromUrl(anyString(), anyBoolean());
        when(blobMock.getBlobUrl()).thenReturn("finalUrl");
    }


    private void saveProductImageInStorageAndCache() throws IOException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(azureProperties.getContainerTemp());
        BlobClient blobClient = containerClient.getBlobClient("temp-image.png");
        blobClient.upload(new ClassPathResource("/assets/testImage.png").getInputStream());

        redisTemplate.opsForHash().put("temporalImages:user-123", "34e3fa03-e59c-49e3-ac3b-d36241bd00a7",
                new TempImage("34e3fa03-e59c-49e3-ac3b-d36241bd00a7", "temp-image.png", blobClient.getBlobUrl(), "user-123"));
    }


    private CreateProductRequest getProductRequestWithoutVariants() {
        try {
            return objectMapper.readValue(getProductRequestWithoutVariantsString(), CreateProductRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private @NotNull String getProductRequestWithoutVariantsString() {
        return """
                {
                  "name": "Grinded coffee beans2",
                  "description": "Grinded coffe beans in a black package with the image of the farm",
                  "category": {
                    "id": 1
                  },
                  "sku": "CAF-MR2",
                  "stock": 55,
                  "status": "AVAILABLE",
                  "basePrice": {
                    "amount": 30.5,
                    "currency": "EUR"
                  },
                  "images": [
                    {
                      "id": "34e3fa03-e59c-49e3-ac3b-d36241bd00a7",
                      "isPrimary": true,
                      "displayOrder": 2
                    }
                  ],
                  "tags": [
                    {
                      "id": 1
                    }
                  ]
                }
                """;
    }


    private ResponseEntity<Void> postProduct(CreateProductRequest createProductRequest,
                                             String userRole, String userId) {

        RequestEntity<CreateProductRequest> request = createRequest(createProductRequest, userRole, userId);

        return testRestTemplate.exchange(request, Void.class);
    }


    private ResponseEntity<ApiErrorResponse> postProductError(CreateProductRequest createProductRequest,
                                                              String userRole, String userId) {

        RequestEntity<CreateProductRequest> request = createRequest(createProductRequest, userRole, userId);

        return testRestTemplate.exchange(request, ApiErrorResponse.class);
    }


    private static @NotNull RequestEntity<CreateProductRequest> createRequest(CreateProductRequest createProductRequest
            , String userRole, String userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("App-User-Roles", userRole);
        headers.add("App-User-ID", userId);


        return RequestEntity
                .post(URI.create("/v1/products"))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(createProductRequest);
    }


}
