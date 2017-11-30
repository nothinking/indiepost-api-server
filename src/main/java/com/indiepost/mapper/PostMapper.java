package com.indiepost.mapper;

import com.indiepost.dto.ImageSetDto;
import com.indiepost.dto.post.AdminPostRequestDto;
import com.indiepost.dto.post.PostDto;
import com.indiepost.dto.post.PostSummaryDto;
import com.indiepost.enums.Types.PostStatus;
import com.indiepost.model.Contributor;
import com.indiepost.model.ImageSet;
import com.indiepost.model.Post;
import com.indiepost.model.Tag;
import org.springframework.beans.BeanUtils;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by jake on 16. 12. 15.
 */
public class PostMapper {

    // copy except id, tags, contributor
    public static Post duplicate(Post srcPost) {
        Post destPost = new Post();
        destPost.setTitle(srcPost.getTitle());
        destPost.setExcerpt(srcPost.getExcerpt());
        destPost.setContent(srcPost.getContent());
        destPost.setBylineName(srcPost.getBylineName());
        destPost.setStatus(srcPost.getStatus());
        destPost.setCreatedAt(srcPost.getCreatedAt());
        destPost.setPublishedAt(srcPost.getPublishedAt());
        destPost.setModifiedAt(srcPost.getModifiedAt());
        destPost.setSplash(srcPost.isSplash());
        destPost.setFeatured(srcPost.isFeatured());
        destPost.setPicked(srcPost.isPicked());
        destPost.setCategoryId(srcPost.getCategoryId());
        destPost.setCreatorId(srcPost.getCreatorId());
        destPost.setModifiedUserId(srcPost.getModifiedUserId());
        destPost.setTitleImageId(srcPost.getTitleImageId());
        return destPost;
    }

    public static PostSummaryDto toSummaryDto(Post post) {
        PostSummaryDto postSummaryDto = new PostSummaryDto();
        postSummaryDto.setId(post.getId());
        postSummaryDto.setFeatured(post.isFeatured());
        postSummaryDto.setSplash(post.isSplash());
        postSummaryDto.setPicked(post.isPicked());
        postSummaryDto.setTitle(post.getTitle());
        postSummaryDto.setExcerpt(post.getExcerpt());
        postSummaryDto.setBookmarkCount(post.getBookmarkCount());
        postSummaryDto.setLegacyPostId(post.getLegacyPostId());
        postSummaryDto.setBylineName(post.getBylineName());
        return postSummaryDto;
    }

    public static void copyDtoToPost(AdminPostRequestDto requestDto, Post post) {
        if (isNotEmpty(requestDto.getTitle())) {
            post.setTitle(requestDto.getTitle());
        }
        if (isNotEmpty(requestDto.getContent())) {
            post.setContent(requestDto.getContent());
        }
        if (isNotEmpty(requestDto.getExcerpt())) {
            post.setExcerpt(requestDto.getExcerpt());
        }
        if (requestDto.getPublishedAt() != null) {
            post.setPublishedAt(requestDto.getPublishedAt());
        }
        if (isNotEmpty(requestDto.getBylineName())) {
            post.setBylineName(requestDto.getBylineName());
        }
        if (requestDto.getCategoryId() != null) {
            post.setCategoryId(requestDto.getCategoryId());
        }
        if (requestDto.getTitleImageId() != null) {
            post.setTitleImageId(requestDto.getTitleImageId());
        }
        if (requestDto.getStatus() != null) {
            post.setStatus(PostStatus.valueOf(requestDto.getStatus()));
        }
        post.setSplash(requestDto.isSplash());
        post.setFeatured(requestDto.isFeatured());
        post.setPicked(requestDto.isPicked());
    }

    public static void addTagsToPost(Post post, List<Tag> tags) {
        if (tags == null) {
            return;
        }
        post.getPostTags().clear();
        int priority = 0;
        for (Tag tag : tags) {
            post.addTag(tag, priority++);
        }
    }

    public static void addContributorsToPost(Post post, List<Contributor> contributors) {
        if (contributors == null) {
            return;
        }
        post.getPostContributors().clear();
        int priority = 0;
        for (Contributor contributor : contributors) {
            post.addContributor(contributor, priority++);
        }
    }

    public static PostDto postToPostDto(Post post) {
        PostDto postDto = new PostDto();
        BeanUtils.copyProperties(post, postDto);
        return postDto;
    }

    public static Post copyDtoToPost(AdminPostRequestDto dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setPublishedAt(dto.getPublishedAt());
        post.setContent(dto.getContent());
        post.setExcerpt(dto.getExcerpt());
        post.setBylineName(dto.getBylineName());

        post.setSplash(dto.isSplash());
        post.setFeatured(dto.isFeatured());
        post.setPicked(dto.isPicked());

        post.setTitleImageId(dto.getTitleImageId());
        post.setCategoryId(dto.getCategoryId());
        return post;
    }

    public static ImageSetDto imageSetToDto(ImageSet imageSet) {
        ImageSetDto imageSetDto = new ImageSetDto();
        imageSetDto.setId(imageSet.getId());
        if (imageSet.getOriginal() != null) {
            imageSetDto.setOriginal(imageSet.getOriginal().getFilePath());
        }
        if (imageSet.getLarge() != null) {
            imageSetDto.setLarge(imageSet.getLarge().getFilePath());
        }
        if (imageSet.getOptimized() != null) {
            imageSetDto.setOptimized(imageSet.getOptimized().getFilePath());
        }
        if (imageSet.getSmall() != null) {
            imageSetDto.setSmall(imageSet.getSmall().getFilePath());
        }
        if (imageSet.getThumbnail() != null) {
            imageSetDto.setThumbnail(imageSet.getThumbnail().getFilePath());
        }
        return imageSetDto;
    }
}
