package com.cordilleracoffee.product.infrastructure.persistence.impl;

import com.cordilleracoffee.product.domain.repository.TagRepository;
import com.cordilleracoffee.product.infrastructure.persistence.TagJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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
