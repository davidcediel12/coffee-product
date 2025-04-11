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

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisImageRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(TemporalImage temporalImage) {

        TempImage tempImage = new TempImage(temporalImage.id(), temporalImage.name(),
                temporalImage.url(), temporalImage.userId());

        String userTemporalImagesKey = "temporalImages:" + temporalImage.userId();

        redisTemplate.opsForHash()
                .put(userTemporalImagesKey, tempImage.id(), tempImage);

        redisTemplate.expire(userTemporalImagesKey, Duration.ofMinutes(EXPIRATION_TIME_MINUTES));
    }
}
