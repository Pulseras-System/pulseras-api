package com.pulseras.api.service;

import com.pulseras.api.dto.CreatePromotionDto;
import com.pulseras.api.dto.PromotionDto;

import java.util.Map;

public interface PromotionService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    PromotionDto getById(String id);
    PromotionDto create(CreatePromotionDto dto);
    PromotionDto update(String id, CreatePromotionDto dto);
    void delete(String id);
}
