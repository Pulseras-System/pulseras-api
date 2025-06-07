package com.pulseras.api.service;

import com.pulseras.api.dto.CreateRatingDto;
import com.pulseras.api.dto.RatingDto;

import java.util.Map;

public interface RatingService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    RatingDto getById(String id);
    void create(CreateRatingDto dto);
    void update(String id, CreateRatingDto dto);
    void delete(String id);
}
