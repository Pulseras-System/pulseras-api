package com.pulseras.api.service;

import com.pulseras.api.dto.CreateNotificationDTO;
import com.pulseras.api.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAllNotifications();
    NotificationDTO getNotificationById(String id);
    NotificationDTO createNotification(CreateNotificationDTO dto);
    NotificationDTO updateNotification(String id, CreateNotificationDTO dto);
    void deleteNotification(String id);
}
