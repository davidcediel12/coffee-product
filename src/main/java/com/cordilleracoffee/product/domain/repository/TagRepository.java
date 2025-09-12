package com.cordilleracoffee.product.domain.repository;

import java.util.List;

public interface TagRepository {

    boolean existsAll(List<Long> ids);
}
