package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.TemporalImage;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisImageStorageRepositoryTest {

    @Mock
    RedisTemplate<String, TempImage> tempImageTemplate;

    @InjectMocks
    RedisImageRepository redisImageRepository;

    HashOperations<String, Object, Object> hashOperations = Mockito.mock(HashOperations.class);


    @Test
    void shouldSaveImage() {

        TemporalImage image = new TemporalImage("212", "2nn2.png", "http://123.com", "292902-190292");


        when(tempImageTemplate.opsForHash()).thenReturn(hashOperations);

        doNothing().when(hashOperations).put(anyString(), eq(image.id()), any(TempImage.class));
        assertDoesNotThrow(() -> redisImageRepository.save(image));


        verify(tempImageTemplate).opsForHash();
        verify(hashOperations).put(anyString(), anyString(), any());
        verify(tempImageTemplate).expire(anyString(), any(Duration.class));


    }


    @Test
    void shouldRetrieveImages() {

        String userId = "user-123";

        TemporalImage expectedImage = new TemporalImage("1", "image1.png", "20921", userId);

        when(tempImageTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(anyString())).thenReturn(Map.of("1", new TempImage(
                expectedImage.id(), expectedImage.name(), expectedImage.url(), userId)));


        Map<String, TemporalImage> images = redisImageRepository.getTemporalImages(userId);

        assertThat(images).isEqualTo(Map.of("1", expectedImage));

    }

}
