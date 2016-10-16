package com.indiepost.viewModel.cms;

/**
 * Created by jake on 10/8/16.
 */
public class CategoryMeta {

    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
