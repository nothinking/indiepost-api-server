package com.indiepost.dto;

import java.util.List;

/**
 * Created by jake on 10/8/16.
 */
public class AdminInitResponseDto {

    private List<UserDto> authors;

    private List<String> authorNames;

    private List<TagDto> tags;

    private UserDto currentUser;

    private List<CategoryDto> categories;

    private List<ContributorDto> contributors;

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public List<UserDto> getAuthors() {
        return authors;
    }

    public void setAuthors(List<UserDto> authors) {
        this.authors = authors;
    }

    public UserDto getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDto currentUser) {
        this.currentUser = currentUser;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public List<ContributorDto> getContributors() {
        return contributors;
    }

    public void setContributors(List<ContributorDto> contributors) {
        this.contributors = contributors;
    }
}