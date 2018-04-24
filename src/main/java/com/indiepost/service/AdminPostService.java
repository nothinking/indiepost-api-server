package com.indiepost.service;

import com.indiepost.dto.post.*;
import com.indiepost.enums.Types;
import com.indiepost.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by jake on 17. 1. 14.
 */
public interface AdminPostService {

    AdminPostResponseDto findOne(Long id);

    AdminPostResponseDto createAutosave();

    AdminPostResponseDto createAutosaveFromPost(Long postId);

    void update(AdminPostRequestDto adminPostRequestDto);

    void deleteById(Long id);

    void delete(Post post);

    Page<AdminPostSummaryDto> find(Types.PostStatus status, Pageable pageable);

    Page<AdminPostSummaryDto> fullTextSearch(String text, Types.PostStatus status,
                                             Pageable pageable);

    Long count();

    Long count(PostQuery query);

    List<String> findAllBylineNames();

    void bulkDeleteByStatus(Types.PostStatus status);

    void bulkDeleteByIds(BulkStatusUpdateDto bulkStatusUpdateDto);

    void bulkStatusUpdate(BulkStatusUpdateDto bulkStatusUpdateDto);
}
