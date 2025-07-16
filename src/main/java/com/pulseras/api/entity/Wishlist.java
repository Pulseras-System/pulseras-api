package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlists")
public class Wishlist {
    @Id
    private ObjectId wishlistId;
    private ObjectId accountId;
    private String productId;
    private int status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
