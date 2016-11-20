package com.indiepost.service;

import com.indiepost.model.Tag;
import com.indiepost.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jake on 9/17/16.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void save(Tag tag) {
        tagRepository.save(tag);
    }

    @Override
    public Tag findById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Tag findByName(String name) {
        return tagRepository.findByTagName(name);
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<String> findAllToStringList() {
        List<Tag> tags = findAll();
        List<String> result = new ArrayList<>();
        for (Tag tag : tags) {
            result.add(tag.getName());
        }
        return result;
    }

    @Override
    public List<Tag> findAll(int page, int maxResults) {
        return tagRepository.findAll(new PageRequest(page, maxResults, Sort.Direction.DESC, "id"));
    }

    @Override
    public void update(Tag tag) {
        tagRepository.update(tag);
    }

    @Override
    public void delete(Tag tag) {
        tagRepository.delete(tag);
    }


}