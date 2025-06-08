package com.pulseras.api.service;

import com.pulseras.api.dto.CreateRoleDTO;
import com.pulseras.api.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getAllRoles();
    RoleDTO getRoleById(String id);
    RoleDTO createRole(CreateRoleDTO dto);
    RoleDTO updateRole(String id, CreateRoleDTO dto);
    void deleteRole(String id);
}
