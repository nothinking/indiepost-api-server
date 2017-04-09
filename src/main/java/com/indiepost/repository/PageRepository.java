package com.indiepost.repository;

import com.indiepost.model.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by jake on 17. 3. 5.
 */
public interface PageRepository {

    Long save(Page page);

    Page findById(Long id);

    void update(Page page);

    void delete(Page page);

    List find(Pageable pageable);

    Long count();

    Page findBySlug(String slug);
}