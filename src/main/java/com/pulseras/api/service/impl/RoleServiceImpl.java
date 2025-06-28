package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateRoleDTO;
import com.pulseras.api.dto.RoleDTO;
import com.pulseras.api.dto.UpdateRoleDTO;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.RoleMapper;
import com.pulseras.api.entity.Role;
import com.pulseras.api.repository.RoleRepository;
import com.pulseras.api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleById(String id) {
        Role role = roleRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return RoleMapper.toDTO(role);
    }

    @Override
    public RoleDTO createRole(CreateRoleDTO dto) {
        Role role = RoleMapper.toEntity(dto);
        return RoleMapper.toDTO(roleRepository.save(role));
    }

    @Override
    public RoleDTO updateRole(String id, CreateRoleDTO dto) {
        Role existing = roleRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        existing.setRoleName(dto.getRoleName());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited());

        return RoleMapper.toDTO(roleRepository.save(existing));
    }

    @Override
    public void deleteRole(String id) {
        ObjectId objId = new ObjectId(id);
        if (!roleRepository.existsById(objId)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(objId);
    }

    @Override
    public RoleDTO getRoleByRoleName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with roleName: " + roleName));
        return RoleMapper.toDTO(role);
    }
    @Override
    public RoleDTO partialUpdateRole(String id, UpdateRoleDTO dto) {
        Role existing = roleRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (dto.getRoleName() != null) existing.setRoleName(dto.getRoleName());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        existing.setLastEdited(dto.getLastEdited() != null ? dto.getLastEdited() : java.time.LocalDateTime.now());

        return RoleMapper.toDTO(roleRepository.save(existing));
    }

}
