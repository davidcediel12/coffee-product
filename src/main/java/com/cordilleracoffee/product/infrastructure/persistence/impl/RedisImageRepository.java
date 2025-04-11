package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.model.TemporalImage;
import com.cordilleracoffee.product.domain.repository.ImageRepository;
import com.cordilleracoffee.product.infrastructure.persistence.TempImageRepository;
import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import org.springframework.stereotype.Repository;

@Repository
public class RedisImageRepository implements ImageRepository {

    private final TempImageRepository tempImageRepository;

    public RedisImageRepository(TempImageRepository tempImageRepository) {
        this.tempImageRepository = tempImageRepository;
    }

    @Override
    public void save(TemporalImage temporalImage) {

        TempImage tempImage = new TempImage();
        tempImage.setId(temporalImage.id());
        tempImage.setName(temporalImage.name());
        tempImage.setUrl(temporalImage.url());
        tempImage.setUserId(temporalImage.userId());

        tempImageRepository.save(tempImage);
    }
}
