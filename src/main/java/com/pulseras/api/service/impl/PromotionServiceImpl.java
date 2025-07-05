package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreatePromotionDto;
import com.pulseras.api.dto.PromotionDto;
import com.pulseras.api.entity.Promotion;
import com.pulseras.api.entity.Product;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.PromotionMapper;
import com.pulseras.api.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    public PromotionServiceImpl(PromotionRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
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
                .map(p -> {
                    Product product = productRepository.findById(p.getProductId()).orElse(null);
                    return PromotionMapper.toDto(p, product != null ? product.getProductName() : null);
                })
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

        Product product = productRepository.findById(promo.getProductId()).orElse(null);
        return PromotionMapper.toDto(promo, product != null ? product.getProductName() : null);
    }

    @Override
    public PromotionDto create(CreatePromotionDto dto) {
        Promotion entity = PromotionMapper.toEntity(dto);
        entity.setCreateDate(LocalDateTime.now());
        Promotion saved = repository.save(entity);

        Product product = productRepository.findById(saved.getProductId()).orElse(null);
        return PromotionMapper.toDto(saved, product != null ? product.getProductName() : null);
    }

    @Override
    public PromotionDto update(String id, CreatePromotionDto dto) {
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

        Promotion updated = repository.save(existing);
        Product product = productRepository.findById(updated.getProductId()).orElse(null);
        return PromotionMapper.toDto(updated, product != null ? product.getProductName() : null);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion not found");
        }
        repository.deleteById(id);
    }
}