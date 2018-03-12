package com.indiepost.service;

import com.indiepost.dto.ContributorDto;
import com.indiepost.enums.Types;
import com.indiepost.model.Contributor;
import com.indiepost.repository.ContributorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.indiepost.service.mapper.ContributorMapper.toDto;
import static com.indiepost.service.mapper.ContributorMapper.toEntity;

@Service
@Transactional
public class ContributorServiceImpl implements ContributorService {

    private final ContributorRepository contributorRepository;

    @Autowired
    public ContributorServiceImpl(ContributorRepository contributorRepository) {
        this.contributorRepository = contributorRepository;
    }

    @Override
    public ContributorDto findOne(Long id) {
        Contributor contributor = contributorRepository.findOne(id);
        return toDto(contributor);
    }

    @Override
    public Long save(ContributorDto dto) {
        Contributor contributor = toEntity(dto);
        contributorRepository.save(contributor);
        return contributor.getId();
    }

    @Override
    public void delete(ContributorDto dto) {
        contributorRepository.delete(dto.getId());
    }

    @Override
    public void deleteById(Long id) {
        contributorRepository.delete(id);
    }

    @Override
    public int count(Types.ContributorType type) {
        return contributorRepository.countAllByContributorType(type).intValue();
    }

    @Override
    public Page<ContributorDto> find(Types.ContributorType type, Pageable pageable) {
        int count = count(type);
        if (count == 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        List<Contributor> contributorList = contributorRepository.findAllByContributorType(type, pageable);
        List<ContributorDto> dtoList = contributorList
                .stream()
                .map(contributor -> toDto(contributor))
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, count);

    }
}