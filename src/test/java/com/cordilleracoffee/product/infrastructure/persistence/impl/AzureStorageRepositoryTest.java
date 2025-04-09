package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.cordilleracoffee.product.infrastructure.persistence.BlobClientFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AzureStorageRepositoryTest {


    @Mock
    private BlobClientFactory blobClientFactory;

    @Mock
    private BlobClient blobClient;

    @InjectMocks
    private AzureStorageRepository azureStorageRepository;

    @Test
    @DisplayName("should return a valid SAS URL when inputs are valid")
    void shouldReturnValidSasUrlWhenInputsAreValid() {
        String folder = "images";
        String fileName = "test.jpg";
        int expirationMinutes = 30;
        String blobUrl = "https://example.blob.core.windows.net/images/test.jpg";
        String sasToken = "sv=2023-01-01&sig=example";

        when(blobClientFactory.createBlobClient(folder, fileName)).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn(blobUrl);
        when(blobClient.generateSas(any(BlobServiceSasSignatureValues.class))).thenReturn(sasToken);

        String result = azureStorageRepository.generateImageUploadUrl(folder, fileName, expirationMinutes);

        assertEquals(blobUrl + "?" + sasToken, result);

        verify(blobClientFactory).createBlobClient(folder, fileName);
        verify(blobClient).getBlobUrl();
        verify(blobClient).generateSas(any(BlobServiceSasSignatureValues.class));
    }

    @Test
    @DisplayName("should throw an exception when expirationMinutes is null")
    void shouldThrowExceptionWhenExpirationMinutesIsNull() {
        String folder = "images";
        String fileName = "test.jpg";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () ->
                        azureStorageRepository.generateImageUploadUrl(folder, fileName, null)
                );

        assertEquals("Expiration minutes must be a non-null positive number", exception.getMessage());
        verifyNoInteractions(blobClientFactory);
    }

    @Test
    @DisplayName("should throw an exception when folder or fileName is empty")
    void shouldThrowExceptionWhenFolderOrFileNameIsEmpty() {
        IllegalArgumentException exception1 =
                assertThrows(IllegalArgumentException.class, () ->
                        azureStorageRepository.generateImageUploadUrl("", "test.jpg", 30)
                );

        IllegalArgumentException exception2 =
                assertThrows(IllegalArgumentException.class, () ->
                        azureStorageRepository.generateImageUploadUrl("images", "", 30)
                );

        assertEquals("Folder and fileName cannot be empty", exception1.getMessage());
        assertEquals("Folder and fileName cannot be empty", exception2.getMessage());
        verifyNoInteractions(blobClientFactory);
    }

    @Test
    void shouldNotGenerateUrlWhenTimeIsMoreThan60Minutes(){

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            azureStorageRepository.generateImageUploadUrl("images", "test.jpg", 61);
        });

        assertEquals("Expiration should be less or equal than 60 minutes", exception.getMessage());

        verifyNoInteractions(blobClientFactory);
    }
}