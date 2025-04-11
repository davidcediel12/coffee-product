package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.TemporalImage;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

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

    @Test
    void shouldSaveImage() {

        ValueOperations<String, TempImage> valueOperations = Mockito.mock(ValueOperations.class);

        when(tempImageTemplate.opsForValue()).thenReturn(valueOperations);

        doNothing().when(valueOperations).set(anyString(), any(), any(Duration.class));

        TemporalImage image = new TemporalImage("212", "2nn2.png", "http://123.com", "292902-190292");
        assertDoesNotThrow(() -> redisImageRepository.save(image));


        verify(tempImageTemplate).opsForValue();
        verify(valueOperations).set(anyString(), any(), any(Duration.class));


    }

}
