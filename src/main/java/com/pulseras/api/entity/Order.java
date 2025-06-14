package com.pulseras.api.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "orders")
@CompoundIndex(def = "{'orderInfor': 1, 'amount': 1}")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private ObjectId id;

    private String orderInfor;
    private Integer amount;
    private String accountId;
    private String voucherId;
    private Double totalPrice;
    private Integer status;
    private LocalDateTime lastEdited;
    private LocalDateTime createDate;
}
