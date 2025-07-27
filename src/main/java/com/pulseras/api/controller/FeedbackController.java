package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;
import com.pulseras.api.dto.SendFeedbackDTO;
import com.pulseras.api.service.EmailService;
import com.pulseras.api.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
@Slf4j
public class FeedbackController {

    private final FeedbackService service;
    private final EmailService emailService;

    public FeedbackController(FeedbackService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
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

    @PostMapping("/send")
    public ResponseEntity<?> sendFeedback(@Valid @RequestBody SendFeedbackDTO dto) {
        try {
            emailService.sendFeedbackEmail(
                dto.getUserEmail(),
                dto.getUserName(),
                dto.getSubject(),
                dto.getContent()
            );
            
            log.info("Feedback email sent successfully from: {}", dto.getUserEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Your feedback has been sent successfully. Thank you for your input!"
            ));
        } catch (Exception e) {
            log.error("Failed to send feedback email from: {}", dto.getUserEmail(), e);
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to send feedback. Please try again later.",
                "error", e.getMessage()
            ));
        }
    }
}