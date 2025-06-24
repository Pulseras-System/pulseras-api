package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateVoucherDTO;
import com.pulseras.api.dto.VoucherDTO;
import com.pulseras.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService service;

    @GetMapping
    public List<VoucherDTO> getAll() {
        return service.getAllVouchers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getVoucherById(id));
    }

    @PostMapping
    public ResponseEntity<VoucherDTO> create(@RequestBody CreateVoucherDTO dto) {
        return ResponseEntity.ok(service.createVoucher(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VoucherDTO> update(@PathVariable String id, @RequestBody CreateVoucherDTO dto) {
        return ResponseEntity.ok(service.updateVoucher(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }
}
