package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.application.FileStorageRepository;
import com.cordilleracoffee.product.application.exception.UnauthorizedUserException;
import com.cordilleracoffee.product.domain.model.UserRole;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.generateurl.SignedUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadImageServiceImplTest {


    @Mock
    FileStorageRepository fileStorageRepository;

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    UploadImageServiceImpl uploadImageService;


    @Test
    void shouldReturnSignedUrl() {

        String url = "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image1.png?sv=mockSas";

        when(fileStorageRepository.generateImageUploadUrl(anyString(), anyString(), anyInt()))
                .thenReturn(url);

        ImageUrlRequests urlRequests = new ImageUrlRequests(List.of(new ImageUrlRequest("image1.png")));

        List<SignedUrl> urls = uploadImageService.getSignedUrls(urlRequests, "user-123", List.of(UserRole.SELLER));

        assertThat(urls).hasSize(1);

        SignedUrl signedUrl = urls.getFirst();
        assertDoesNotThrow(() -> UUID.fromString(signedUrl.id()));

        assertThat(signedUrl.url()).isEqualTo(url);
    }


    @Test
    void shouldReturnSignedUrlForMultipleImages() {

        when(fileStorageRepository.generateImageUploadUrl(anyString(), anyString(), anyInt()))
                .thenReturn("https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image1.png?sv=mockSas")
                .thenReturn("https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image2.png?sv=mockSas");

        ImageUrlRequests urlRequests = new ImageUrlRequests(List.of(
                new ImageUrlRequest("image1.png"),
                new ImageUrlRequest("image2.png")
        ));

        List<SignedUrl> urls = uploadImageService.getSignedUrls(urlRequests, "user-123", List.of(UserRole.SELLER));

        assertThat(urls).hasSize(2);

        SignedUrl signedUrl = urls.getFirst();
        assertDoesNotThrow(() -> UUID.fromString(signedUrl.id()));
        assertThat(signedUrl.url()).isEqualTo("https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image1.png?sv=mockSas");

        SignedUrl secondSignedUrl = urls.get(1);

        assertDoesNotThrow(() -> UUID.fromString(secondSignedUrl.id()));
        assertThat(secondSignedUrl.url()).isEqualTo("https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image2.png?sv=mockSas");

    }


    @Test
    void shouldSaveTemporalImageInCache() {

        String url = "https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image1.png?sv=mockSas";

        when(fileStorageRepository.generateImageUploadUrl(anyString(), anyString(), anyInt()))
                .thenReturn(url);

        ImageUrlRequests urlRequests = new ImageUrlRequests(List.of(new ImageUrlRequest("image1.png")));

        String userId = "user-123";

        List<SignedUrl> urls = uploadImageService.getSignedUrls(urlRequests, userId, List.of(UserRole.SELLER));

        assertThat(urls).hasSize(1);

        SignedUrl signedUrl = urls.getFirst();

        verify(imageRepository)
                .save(argThat(tempImage -> tempImage.id().equals(signedUrl.id()) &&
                        tempImage.url().equals(signedUrl.url()) && tempImage.userId().equals(userId)));
    }


    @Test
    void shouldNotGenerateUrlWhenUserIsNotSellerOrAdmin() {
        ImageUrlRequests urlRequests = new ImageUrlRequests(List.of(new ImageUrlRequest("image3.png")));
        List<UserRole> userRoles = List.of(UserRole.CUSTOMER);

        assertThrows(UnauthorizedUserException.class, () -> uploadImageService.getSignedUrls(
                urlRequests, "user-123", userRoles
        ));
    }
}
