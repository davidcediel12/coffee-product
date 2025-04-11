package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.TemporalImage;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisImageRepository implements ImageRepository {

    private static final long EXPIRATION_TIME_MINUTES = 5L;

    private final RedisTemplate<String, TempImage> redisTemplate;

    public RedisImageRepository(RedisTemplate<String, TempImage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(TemporalImage temporalImage) {

        TempImage tempImage = new TempImage();
        tempImage.setId(temporalImage.id());
        tempImage.setName(temporalImage.name());
        tempImage.setUrl(temporalImage.url());
        tempImage.setUserId(temporalImage.userId());

        String temporalImageKey = "temporalImage:" + temporalImage.id();

        redisTemplate.opsForValue().set(temporalImageKey, tempImage, Duration.ofMinutes(EXPIRATION_TIME_MINUTES));
    }
}
