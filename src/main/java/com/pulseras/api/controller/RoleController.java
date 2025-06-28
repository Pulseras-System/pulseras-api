package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateRoleDTO;
import com.pulseras.api.dto.RoleDTO;
import com.pulseras.api.dto.UpdateRoleDTO;
import com.pulseras.api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleDTO> getAll() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/role-name/{name}")
    public ResponseEntity<RoleDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.getRoleByRoleName(name));
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody CreateRoleDTO dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable String id, @RequestBody CreateRoleDTO dto) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<RoleDTO> partialUpdate(@PathVariable String id, @RequestBody UpdateRoleDTO dto) {
        return ResponseEntity.ok(roleService.partialUpdateRole(id, dto));
    }

}
