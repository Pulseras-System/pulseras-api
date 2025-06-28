package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateNotificationDTO;
import com.pulseras.api.dto.NotificationDTO;
import com.pulseras.api.dto.UpdateNotificationDTO;
import com.pulseras.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDTO> getAll() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> create(@RequestBody CreateNotificationDTO dto) {
        return ResponseEntity.ok(notificationService.createNotification(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> update(@PathVariable String id, @RequestBody CreateNotificationDTO dto) {
        return ResponseEntity.ok(notificationService.updateNotification(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<NotificationDTO> partialUpdate(@PathVariable String id,
                                                         @RequestBody UpdateNotificationDTO dto) {
        return ResponseEntity.ok(notificationService.partialUpdateNotification(id, dto));
    }

}

