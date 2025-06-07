package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;
import com.pulseras.api.entity.Feedback;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.FeedbackMapper;
import com.pulseras.api.repository.FeedbackRepository;
import com.pulseras.api.service.FeedbackService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository repository;

    public FeedbackServiceImpl(FeedbackRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> getAll(String keyword, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Feedback> result;

        if (keyword != null && !keyword.isEmpty()) {
            result = new PageImpl<>(repository.findByFeedbackInforContainingIgnoreCase(keyword, pageable));
        } else {
            result = repository.findAll(pageable);
        }

        List<FeedbackDto> content = result.getContent().stream()
                .map(FeedbackMapper::toDto)
                .toList();

        return Map.of(
                "items", content,
                "totalPages", result.getTotalPages(),
                "totalItems", result.getTotalElements()
        );
    }

    @Override
    public FeedbackDto getById(String id) {
        Feedback f = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));
        return FeedbackMapper.toDto(f);
    }

    @Override
    public void create(CreateFeedbackDto dto) {
        Feedback f = FeedbackMapper.toEntity(dto);
        f.setCreateDate(LocalDateTime.now());
        repository.save(f);
    }

    @Override
    public void update(String id, CreateFeedbackDto dto) {
        Feedback f = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        f.setAccountId(dto.getAccountId());
        f.setProductId(dto.getProductId());
        f.setFeedbackInfor(dto.getFeedbackInfor());
        f.setStatus(dto.getStatus());
        f.setLastEdited(LocalDateTime.now());

        repository.save(f);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback not found");
        }
        repository.deleteById(id);
    }
}
