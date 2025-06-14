package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    private ObjectId id;

    private String orderId;
    private String productId;
    private Integer quantity;
    private Double price;
    private Integer promotionId;
    private Integer status;
    private LocalDateTime createDate;
    private LocalDateTime lastEdited;
}
