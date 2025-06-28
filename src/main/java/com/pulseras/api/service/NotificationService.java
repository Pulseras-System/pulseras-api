package com.pulseras.api.service;

import com.pulseras.api.dto.CreateNotificationDTO;
import com.pulseras.api.dto.NotificationDTO;
import com.pulseras.api.dto.UpdateNotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAllNotifications();
    NotificationDTO getNotificationById(String id);
    NotificationDTO createNotification(CreateNotificationDTO dto);
    NotificationDTO updateNotification(String id, CreateNotificationDTO dto);
    void deleteNotification(String id);
    NotificationDTO partialUpdateNotification(String id, UpdateNotificationDTO dto);

}
