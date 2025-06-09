package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateRoleDTO;
import com.pulseras.api.dto.RoleDTO;
import com.pulseras.api.entity.Role;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class RoleMapper {

    public static RoleDTO toDTO(Role entity) {
        return RoleDTO.builder()
                .id(entity.getId().toHexString())
                .roleName(entity.getRoleName())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .lastEdited(entity.getLastEdited())
                .build();
    }

    public static Role toEntity(CreateRoleDTO dto) {
        return Role.builder()
                .id(new ObjectId())
                .roleName(dto.getRoleName())
                .status(dto.getStatus())
                .lastEdited(dto.getLastEdited())
                .createdDate(LocalDateTime.now())
                .build();
    }
}
