package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateRatingDto;
import com.pulseras.api.dto.RatingDto;
import com.pulseras.api.entity.Rating;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.RatingMapper;
import com.pulseras.api.repository.RatingRepository;
import com.pulseras.api.service.RatingService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;

    public RatingServiceImpl(RatingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> getAll(String keyword, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Rating> result;

        if (keyword != null && !keyword.isEmpty()) {
            result = new PageImpl<>(repository.findByCommentContainingIgnoreCase(keyword, pageable));
        } else {
            result = repository.findAll(pageable);
        }

        List<RatingDto> content = result.getContent().stream()
                .map(RatingMapper::toDto)
                .toList();

        return Map.of(
                "items", content,
                "totalPages", result.getTotalPages(),
                "totalItems", result.getTotalElements()
        );
    }

    @Override
    public RatingDto getById(String id) {
        Rating rating = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));
        return RatingMapper.toDto(rating);
    }

    @Override
    public void create(CreateRatingDto dto) {
        Rating entity = RatingMapper.toEntity(dto);
        entity.setCreateDate(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    public void update(String id, CreateRatingDto dto) {
        Rating existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        existing.setAccountId(dto.getAccountId());
        existing.setProductId(dto.getProductId());
        existing.setComment(dto.getComment());
        existing.setRating(dto.getRating());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(LocalDateTime.now());

        repository.save(existing);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Rating not found");
        }
        repository.deleteById(id);
    }
}
