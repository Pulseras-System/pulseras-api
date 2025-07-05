package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;
import com.pulseras.api.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getAll(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sort
    ) {
        return service.getAll(keyword, page, size, sort);
    }

    @GetMapping("/{id}")
    public FeedbackDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public FeedbackDto create(@Valid @RequestBody CreateFeedbackDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public FeedbackDto update(@PathVariable String id, @Valid @RequestBody CreateFeedbackDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}