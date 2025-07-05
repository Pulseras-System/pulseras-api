package com.pulseras.api.service;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;

import java.util.Map;

public interface FeedbackService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    FeedbackDto getById(String id);
    FeedbackDto create(CreateFeedbackDto dto);
    FeedbackDto update(String id, CreateFeedbackDto dto);
    void delete(String id);
}
