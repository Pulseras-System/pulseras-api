package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Document(collection = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private ObjectId id;

    @Indexed
    private String fullName;

    private String password;

    @Indexed(unique = true)
    private String username;

    private String phone;

    @Indexed(unique = true)
    private String email;

    private  String roleId;

    private LocalDateTime createDate;

    private LocalDateTime lastEdited;

    private Integer status;

    @Nullable
    private String banReason;
}
