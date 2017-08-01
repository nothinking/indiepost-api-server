package com.indiepost.service;

import com.indiepost.dto.PostDto;
import com.indiepost.dto.PostQuery;
import com.indiepost.dto.PostSummary;
import com.indiepost.dto.RelatedPostResponseDto;
import com.indiepost.dto.stat.PostStat;
import com.indiepost.enums.Types.PostStatus;
import com.indiepost.model.Image;
import com.indiepost.model.ImageSet;
import com.indiepost.model.Post;
import com.indiepost.model.Tag;
import com.indiepost.repository.AnalyticsRepository;
import com.indiepost.repository.ImageRepository;
import com.indiepost.repository.PostRepository;
import com.indiepost.repository.TagRepository;
import com.indiepost.service.mapper.PostMapperService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jake on 7/30/16.
 */
@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final PostMapperService postMapperService;

    private final ImageRepository imageRepository;

    private final TagRepository tagRepository;

    private final AnalyticsRepository analyticsRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, ImageRepository imageRepository,
                           PostMapperService postMapperService, TagRepository tagRepository,
                           AnalyticsRepository analyticsRepository) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.postMapperService = postMapperService;
        this.tagRepository = tagRepository;
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public PostDto findById(Long id) {
        Post post = postRepository.findById(id);
        List<Tag> tagList = post.getTags();
        if (tagList.size() > 0) {
            tagList.get(0);
        }
        ImageSet titleImage = post.getTitleImage();
        if (titleImage != null) {
            titleImage.getOptimized();
        }
        return postMapperService.postToPostDto(post);
    }

    @Override
    public PostDto findByLegacyId(Long id) {
        Post post = postRepository.findByLegacyId(id);
        List<Tag> tagList = post.getTags();
        if (tagList.size() > 0) {
            tagList.get(0);
        }
        post.getTitleImage().getOptimized();
        return postMapperService.postToPostDto(post);
    }

    @Override
    public Long count() {
        return postRepository.count();
    }

    @Override
    public Long count(PostQuery query) {
        return postRepository.count(query);
    }

    @Override
    public List<PostSummary> findByIds(List<Long> ids) {
        List<PostSummary> result = postRepository.findByIds(ids);
        return setTitleImages(result);
    }

    @Override
    public List<PostSummary> findAll(int page, int maxResults, boolean isDesc) {
        List<PostSummary> result = postRepository.findByStatus(PostStatus.PUBLISH, getPageable(page, maxResults, isDesc));
        return setTitleImages(result);
    }

    @Override
    public List<PostSummary> findByQuery(PostQuery query, int page, int maxResults, boolean isDesc) {
        List<PostSummary> result = postRepository.findByQuery(query, getPageable(page, maxResults, isDesc));
        return setTitleImages(result);
    }

    @Override
    public List<PostSummary> findByCategoryId(Long categoryId, int page, int maxResults, boolean isDesc) {
        List<PostSummary> result = postRepository.findByCategoryId(categoryId, getPageable(page, maxResults, isDesc));
        return setTitleImages(result);
    }

    @Override
    public List<PostSummary> findByTagName(String tagName) {
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag == null) {
            return null;
        }
        List<Post> postList = tag.getPosts();
        if (postList == null || postList.size() == 0) {
            return null;
        }
        List<PostSummary> dtoList = postList.stream()
                .filter(post -> post.getStatus().equals(PostStatus.PUBLISH))
                .map(postMapperService::postToPostSummaryDto)
                .collect(Collectors.toList());
        return setTitleImages(dtoList);
    }

    @Override
    public List<RelatedPostResponseDto> getRelatedPosts(List<Long> ids, boolean isLegacy, boolean isMobile) {
        List<PostSummary> postSummaryList = this.postRepository.findByIds(ids);
        if (postSummaryList == null) {
            return null;
        }

        this.setTitleImages(postSummaryList);
        String legacyPostMobileUrl = "http://www.indiepost.co.kr/indiepost/ContentView.do?no=";
        String legacyPostWebUrl = "http://www.indiepost.co.kr/ContentView.do?no=";

        List<RelatedPostResponseDto> relatedPostResponseDtoList = new ArrayList<>();
        for (PostSummary postSummary : postSummaryList) {
            RelatedPostResponseDto relatedPostResponseDto = new RelatedPostResponseDto();
            relatedPostResponseDto.setId(postSummary.getId());
            relatedPostResponseDto.setTitle(postSummary.getTitle());
            relatedPostResponseDto.setExcerpt(postSummary.getExcerpt());
            if (postSummary.getTitleImageId() != null) {
                Image image = postSummary.getTitleImage().getThumbnail();
                relatedPostResponseDto.setImageUrl(image.getFilePath());
                relatedPostResponseDto.setImageWidth(image.getWidth());
                relatedPostResponseDto.setImageHeight(image.getHeight());
            }
            if (isLegacy) {
                if (isMobile) {
                    relatedPostResponseDto.setUrl(legacyPostMobileUrl + postSummary.getLegacyPostId());
                } else {
                    relatedPostResponseDto.setUrl(legacyPostWebUrl + postSummary.getLegacyPostId());
                }
            } else {
                relatedPostResponseDto.setUrl("/posts/" + postSummary.getId());
            }
            relatedPostResponseDtoList.add(relatedPostResponseDto);
        }
        return relatedPostResponseDtoList;
    }

    @Override
    public List<PostSummary> getTopRatedPosts(LocalDateTime since, LocalDateTime until, Long limit) {
        List<PostStat> topStats = analyticsRepository.getPostsOrderByUniquePageviews(since, until, limit);
        List<Long> topPostIds = topStats.stream()
                .map(postStat -> postStat.getId())
                .collect(Collectors.toList());

        List<PostSummary> topRatedPosts = postRepository.findByIds(topPostIds);
        return setTitleImages(topRatedPosts);
    }

    @Override
    public List<PostSummary> search(String text, int page, int maxResults) {
        if (text.length() > 30) {
            text = text.substring(0, 30);
        }
        Pageable pageable = getPageable(page, maxResults, true);
        List<Post> postList = postRepository.search(text, pageable);
        return postList.stream()
                .map(post -> {
                    PostSummary dto = new PostSummary();
                    BeanUtils.copyProperties(post, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long findIdByLegacyId(Long legacyId) {
        return postRepository.findIdByLegacyId(legacyId);
    }

    @Override
    public PostSummary findSplashPost() {
        PostQuery query = new PostQuery();
        query.setSplash(true);
        List<PostSummary> posts = findByQuery(query, 0, 1, true);
        return posts == null ? null : posts.get(0);
    }

    @Override
    public PostSummary findFeaturePost() {
        PostQuery query = new PostQuery();
        query.setFeatured(true);
        List<PostSummary> posts = findByQuery(query, 0, 1, true);
        return posts == null ? null : posts.get(0);
    }

    @Override
    public List<PostSummary> findPickedPosts() {
        PostQuery query = new PostQuery();
        query.setPicked(true);
        return findByQuery(query, 0, 8, true);
    }

    private List<PostSummary> setTitleImages(List<PostSummary> postSummaryList) {
        List<Long> ids = postSummaryList.stream()
                .filter(postExcerpt -> postExcerpt.getTitleImageId() != null)
                .map(PostSummary::getTitleImageId)
                .collect(Collectors.toList());

        if (ids.size() == 0) {
            return postSummaryList;
        }

        List<ImageSet> imageSetList = imageRepository.findByIds(ids);

        for (PostSummary postSummary : postSummaryList) {
            Long titleImageId = postSummary.getTitleImageId();
            for (ImageSet imageSet : imageSetList) {
                if (imageSet.getId().equals(titleImageId)) {
                    postSummary.setTitleImage(imageSet);
                    break;
                }
            }
        }

        return postSummaryList;
    }

    private Pageable getPageable(int page, int maxResults, boolean isDesc) {
        return isDesc ?
                new PageRequest(page, maxResults, Sort.Direction.DESC, "publishedAt") :
                new PageRequest(page, maxResults, Sort.Direction.ASC, "publishedAt");
    }
}
