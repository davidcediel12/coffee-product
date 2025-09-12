package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.repository.TagRepository;
import com.cordilleracoffee.product.infrastructure.persistence.TagJpaRepository;

import java.util.List;

public class TagRepositoryImpl implements TagRepository {

    private final TagJpaRepository tagJpaRepository;

    public TagRepositoryImpl(TagJpaRepository tagJpaRepository) {
        this.tagJpaRepository = tagJpaRepository;
    }

    @Override
    public boolean existsAll(List<Long> ids) {
        long existentIds = tagJpaRepository.countByIdIn(ids);

        return existentIds == ids.size();
    }
}
