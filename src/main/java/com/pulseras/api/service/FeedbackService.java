package com.pulseras.api.service;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;

import java.util.Map;

public interface FeedbackService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    FeedbackDto getById(String id);
    void create(CreateFeedbackDto dto);
    void update(String id, CreateFeedbackDto dto);
    void delete(String id);
}
