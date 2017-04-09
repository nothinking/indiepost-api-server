package com.indiepost.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by jake on 17. 2. 26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenderingRequestDto {
    private InitialData initialData;
    private List<PostSummaryDto> posts;
    private PostDto post;
    private PageDto page;
    private String path;

    public RenderingRequestDto() {
    }

    public RenderingRequestDto(InitialData initialData) {
        this.initialData = initialData;
        this.path = "/";
    }

    public RenderingRequestDto(InitialData initialData, String path) {
        this.initialData = initialData;
        this.path = path;
    }

    public RenderingRequestDto(InitialData initialData, PostDto post, String path) {
        this.initialData = initialData;
        this.post = post;
        this.path = path;
    }

    public RenderingRequestDto(InitialData initialData, PageDto page, String path) {
        this.initialData = initialData;
        this.page = page;
        this.path = path;
    }

    public RenderingRequestDto(InitialData initialData, List<PostSummaryDto> posts, String path) {
        this.initialData = initialData;
        this.posts = posts;
        this.path = path;
    }

    public InitialData getInitialData() {
        return initialData;
    }

    public void setInitialData(InitialData initialData) {
        this.initialData = initialData;
    }

    public List<PostSummaryDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostSummaryDto> posts) {
        this.posts = posts;
    }

    public PostDto getPost() {
        return post;
    }

    public void setPost(PostDto post) {
        this.post = post;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(PageDto page) {
        this.page = page;
    }
}