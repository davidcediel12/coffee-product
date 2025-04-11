package com.cordilleracoffee.product.infrastructure.persistence;

import com.cordilleracoffee.product.infrastructure.persistence.entity.TempImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempImageRepository extends CrudRepository<TempImage, String> {
}
