package com.pulseras.api.mapper;

import com.pulseras.api.dto.AccountDTO;
import com.pulseras.api.dto.CreateAccountDTO;
import com.pulseras.api.entity.Account;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class AccountMapper {

    public static AccountDTO toDTO(Account entity) {
        return AccountDTO.builder()
                .id(entity.getId().toHexString())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .roleId(entity.getRoleId())
                .createDate(entity.getCreateDate())
                .lastEdited(entity.getLastEdited())
                .status(entity.getStatus())
                .build();
    }

    public static Account toEntity(CreateAccountDTO dto) {
        return Account.builder()
                .id(new ObjectId())
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .roleId(dto.getRoleId())
                .status(dto.getStatus())
                .createDate(LocalDateTime.now())
                .lastEdited(LocalDateTime.now())
                .build();
    }
}
