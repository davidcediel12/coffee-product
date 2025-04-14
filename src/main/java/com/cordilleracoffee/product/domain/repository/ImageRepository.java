package com.cordilleracoffee.product.domain.repository;

import com.cordilleracoffee.product.domain.model.TemporalImage;

import java.util.Map;

public interface ImageRepository {


    void save(TemporalImage temporalImage);

    Map<String, TemporalImage> getTemporalImages(String userId);
}
