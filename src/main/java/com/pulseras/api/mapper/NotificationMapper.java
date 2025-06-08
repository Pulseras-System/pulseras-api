package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateNotificationDTO;
import com.pulseras.api.dto.NotificationDTO;
import com.pulseras.api.entity.Notification;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification entity) {
        return NotificationDTO.builder()
                .id(entity.getId().toHexString())
                .accountId(entity.getAccountId())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .lastEdited(entity.getLastEdited())
                .createDate(entity.getCreateDate())
                .build();
    }

    public static Notification toEntity(CreateNotificationDTO dto) {
        return Notification.builder()
                .id(new ObjectId())
                .accountId(dto.getAccountId())
                .message(dto.getMessage())
                .status(dto.getStatus())
                .lastEdited(dto.getLastEdited())
                .createDate(LocalDateTime.now())
                .build();
    }
}
