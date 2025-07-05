package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.entity.Feedback;

public class FeedbackMapper {

    public static Feedback toEntity(CreateFeedbackDto dto) {
        Feedback f = new Feedback();
        f.setAccountId(dto.getAccountId());
        f.setProductId(dto.getProductId());
        f.setFeedbackInfor(dto.getFeedbackInfor());
        f.setStatus(dto.getStatus());
        return f;
    }
}
