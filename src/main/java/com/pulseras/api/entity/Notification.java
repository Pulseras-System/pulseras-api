package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    private ObjectId id;

    private String accountId;
    private String message;
    private Integer status;
    private LocalDateTime lastEdited;
    private LocalDateTime createDate;
}
