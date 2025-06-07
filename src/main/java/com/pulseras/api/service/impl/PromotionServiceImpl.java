package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreatePromotionDto;
import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.entity.Promotion;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.PromotionMapper;
import com.pulseras.api.repository.PromotionRepository;
import com.pulseras.api.service.PromotionService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository repository;

    public PromotionServiceImpl(PromotionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> getAll(String keyword, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Promotion> result;

        if (keyword != null && !keyword.isEmpty()) {
            result = new PageImpl<>(repository.findByPromotionNameContainingIgnoreCase(keyword, pageable));
        } else {
            result = repository.findAll(pageable);
        }

        List<PromotionDto> content = result.getContent().stream()
                .map(PromotionMapper::toDto)
                .toList();

        return Map.of(
                "items", content,
                "totalPages", result.getTotalPages(),
                "totalItems", result.getTotalElements()
        );
    }

    @Override
    public PromotionDto getById(String id) {
        Promotion promo = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        return PromotionMapper.toDto(promo);
    }

    @Override
    public void create(CreatePromotionDto dto) {
        Promotion entity = PromotionMapper.toEntity(dto);
        entity.setCreateDate(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    public void update(String id, CreatePromotionDto dto) {
        Promotion existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        existing.setProductId(dto.getProductId());
        existing.setPromotionName(dto.getPromotionName());
        existing.setPromotionDescription(dto.getPromotionDescription());
        existing.setDiscountPercentage(dto.getDiscountPercentage());
        existing.setStartDay(dto.getStartDay());
        existing.setExpireDay(dto.getExpireDay());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(LocalDateTime.now());

        repository.save(existing);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion not found");
        }
        repository.deleteById(id);
    }
}
