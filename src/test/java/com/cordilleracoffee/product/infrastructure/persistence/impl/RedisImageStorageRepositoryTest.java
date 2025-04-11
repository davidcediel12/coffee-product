package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.TemporalImage;
import com.cordilleracoffee.product.infrastructure.persistence.TempImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisImageStorageRepositoryTest {

    @Mock
    TempImageRepository tempImageRepository;

    @InjectMocks
    RedisImageRepository redisImageRepository;

    @Test
    void shouldSaveImage(){

        TemporalImage image = new TemporalImage("212", "2nn2.png", "http://123.com", "292902-190292");
        assertDoesNotThrow(() -> redisImageRepository.save(image));

        verify(tempImageRepository).save(any());


    }

}
