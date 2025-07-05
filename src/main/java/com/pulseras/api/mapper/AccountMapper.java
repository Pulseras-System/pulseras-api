package com.pulseras.api.mapper;

import com.pulseras.api.dto.AccountDTO;
import com.pulseras.api.dto.CreateAccountDTO;
import com.pulseras.api.entity.Account;
import com.pulseras.api.entity.Role;
import com.pulseras.api.repository.RoleRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccountMapper {

    private static RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        AccountMapper.roleRepository = roleRepository;
    }

    public static AccountDTO toDTO(Account entity) {
        String roleName = "";
        if (entity.getRoleId() != null && !entity.getRoleId().trim().isEmpty()) {
            try {
                ObjectId roleObjectId = new ObjectId(entity.getRoleId());
                roleName = roleRepository.findById(roleObjectId)
                        .map(Role::getRoleName)
                        .orElse("");
            } catch (IllegalArgumentException e) {
                // Invalid ObjectId format, keep roleName as empty string
                roleName = "";
            }
        }

        return AccountDTO.builder()
                .id(entity.getId().toHexString())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .roleId(entity.getRoleId())
                .roleName(roleName)
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
