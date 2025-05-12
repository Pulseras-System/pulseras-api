package com.pulseras.api.service.impl;

import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.entity.Promotion;
import com.pulseras.api.mapper.PromotionMapper;
import com.pulseras.api.repository.PromotionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepo repo;

    public List<PromotionDto> findAll() {
        return repo.findAll().stream().map(PromotionMapper::toDto).collect(Collectors.toList());
    }

    public PromotionDto findById(String id) {
        return repo.findById(id).map(PromotionMapper::toDto).orElse(null);
    }

    public PromotionDto create(PromotionDto dto) {
        Promotion saved = repo.save(PromotionMapper.toEntity(dto));
        return PromotionMapper.toDto(saved);
    }

    public PromotionDto update(String id, PromotionDto dto) {
        Promotion existing = repo.findById(id).orElseThrow();
        Promotion updated = PromotionMapper.toEntity(dto);
        updated.setId(id);
        return PromotionMapper.toDto(repo.save(updated));
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
