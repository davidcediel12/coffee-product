package com.cordilleracoffee.product.application.impl;

import com.cordilleracoffee.product.infrastructure.application.impl.UploadImageServiceImpl;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequest;
import com.cordilleracoffee.product.infrastructure.dto.ImageUrlRequests;
import com.cordilleracoffee.product.infrastructure.dto.SignedUrl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UploadImageServiceImplTest {


    UploadImageServiceImpl uploadImageService = new UploadImageServiceImpl();

    @Test
    void shouldReturnSignedUrl() {
        ImageUrlRequests urlRequests = new ImageUrlRequests(List.of(new ImageUrlRequest("image1.png")));

        List<SignedUrl> urls = uploadImageService.getSignedUrls(urlRequests);

        assertThat(urls).hasSize(1);

        SignedUrl signedUrl = urls.get(0);
        assertThat(signedUrl.id()).isEqualTo("id-1");
        assertThat(signedUrl.url()).isEqualTo("https://cordilleracoffee.blob.core.windows.net/cordilleracoffee/image1.png");
    }
}
