package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;
import com.pulseras.api.entity.Feedback;
import com.pulseras.api.entity.Product;
import com.pulseras.api.entity.Account;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.FeedbackMapper;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.repository.FeedbackRepository;
import com.pulseras.api.repository.ProductRepository;
import com.pulseras.api.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository repository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

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
                .map(this::toDtoWithNames)
                .toList();

        return Map.of(
                "items", content,
                "totalPages", result.getTotalPages(),
                "totalItems", result.getTotalElements()
        );
    }

    @Override
    public FeedbackDto getById(String id) {
        Feedback feedback = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));
        return toDtoWithNames(feedback);
    }

    @Override
    public FeedbackDto create(CreateFeedbackDto dto) {
        Feedback f = FeedbackMapper.toEntity(dto);
        f.setCreateDate(LocalDateTime.now());
        Feedback saved = repository.save(f);
        return toDtoWithNames(saved);
    }

    @Override
    public FeedbackDto update(String id, CreateFeedbackDto dto) {
        Feedback f = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        f.setAccountId(dto.getAccountId());
        f.setProductId(dto.getProductId());
        f.setFeedbackInfor(dto.getFeedbackInfor());
        f.setStatus(dto.getStatus());
        f.setLastEdited(LocalDateTime.now());

        Feedback updated = repository.save(f);
        return toDtoWithNames(updated);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback not found");
        }
        repository.deleteById(id);
    }

    private FeedbackDto toDtoWithNames(Feedback f) {
        String productName = null;
        String fullName = null;

        if (f.getProductId() != null && ObjectId.isValid(f.getProductId())) {
            productName = productRepository.findById(f.getProductId())
                    .map(Product::getProductName)
                    .orElse("Unknown Product");
        }

        if (f.getAccountId() != null && ObjectId.isValid(f.getAccountId())) {
            fullName = accountRepository.findById(new ObjectId(f.getAccountId()))
                    .map(Account::getFullName)
                    .orElse("Unknown User");
        }

        return FeedbackDto.builder()
                .feedbackId(f.getFeedbackId())
                .accountId(f.getAccountId())
                .productId(f.getProductId())
                .productName(productName)
                .fullName(fullName)
                .feedbackInfor(f.getFeedbackInfor())
                .status(f.getStatus())
                .lastEdited(f.getLastEdited())
                .createDate(f.getCreateDate())
                .build();
    }
}
