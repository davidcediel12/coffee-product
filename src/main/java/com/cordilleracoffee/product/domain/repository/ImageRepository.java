package com.cordilleracoffee.product.domain.repository;

import com.cordilleracoffee.product.domain.model.TemporalImage;

public interface ImageRepository {


    void save(TemporalImage temporalImage);
}
