package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateFeedbackDto;
import com.pulseras.api.dto.FeedbackDto;
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

    public static FeedbackDto toDto(Feedback f) {
        FeedbackDto dto = new FeedbackDto();
        dto.setFeedbackId(f.getFeedbackId());
        dto.setAccountId(f.getAccountId());
        dto.setProductId(f.getProductId());
        dto.setFeedbackInfor(f.getFeedbackInfor());
        dto.setStatus(f.getStatus());
        dto.setLastEdited(f.getLastEdited());
        dto.setCreateDate(f.getCreateDate());
        return dto;
    }
}
