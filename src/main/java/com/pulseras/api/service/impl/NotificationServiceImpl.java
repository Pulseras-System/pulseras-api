package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateNotificationDTO;
import com.pulseras.api.dto.NotificationDTO;
import com.pulseras.api.dto.UpdateNotificationDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.NotificationMapper;
import com.pulseras.api.entity.Notification;
import com.pulseras.api.repository.NotificationRepository;
import com.pulseras.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO getNotificationById(String id) {
        Notification notification = notificationRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return NotificationMapper.toDTO(notification);
    }

    @Override
    public NotificationDTO createNotification(CreateNotificationDTO dto) {
        Notification notification = NotificationMapper.toEntity(dto);
        return NotificationMapper.toDTO(notificationRepository.save(notification));
    }

    @Override
    public NotificationDTO updateNotification(String id, CreateNotificationDTO dto) {
        Notification existing = notificationRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        existing.setAccountId(dto.getAccountId());
        existing.setMessage(dto.getMessage());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited());

        return NotificationMapper.toDTO(notificationRepository.save(existing));
    }

    @Override
    public void deleteNotification(String id) {
        ObjectId objId = new ObjectId(id);
        if (!notificationRepository.existsById(objId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(objId);
    }
    @Override
    public NotificationDTO partialUpdateNotification(String id, UpdateNotificationDTO dto) {
        Notification existing = notificationRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (dto.getAccountId() != null) existing.setAccountId(dto.getAccountId());
        if (dto.getMessage() != null)   existing.setMessage(dto.getMessage());
        if (dto.getStatus() != null)    existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited() != null
                ? dto.getLastEdited()
                : java.time.LocalDateTime.now());

        return NotificationMapper.toDTO(notificationRepository.save(existing));
    }

}
