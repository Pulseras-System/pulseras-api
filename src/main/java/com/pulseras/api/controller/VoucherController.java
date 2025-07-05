package com.pulseras.api.controller;

import com.pulseras.api.dto.CreateVoucherDTO;
import com.pulseras.api.dto.UpdateVoucherDTO;
import com.pulseras.api.dto.VoucherDTO;
import com.pulseras.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> update(@PathVariable String id, @RequestBody CreateVoucherDTO dto) {
        return ResponseEntity.ok(service.updateVoucher(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<VoucherDTO> partialUpdate(@PathVariable String id,
                                                    @RequestBody UpdateVoucherDTO dto) {
        return ResponseEntity.ok(service.partialUpdateVoucher(id, dto));
    }

    @GetMapping("/account/{accountId}")
    public List<VoucherDTO> getByAccountId(@PathVariable String accountId) {
        return service.getVouchersByAccountId(accountId);
    }

    @GetMapping("/{id}/account/{accountId}")
    public ResponseEntity<VoucherDTO> getByIdAndAccountId(@PathVariable String id, @PathVariable String accountId) {
        return ResponseEntity.ok(service.getVoucherByIdAndAccountId(id, accountId));
    }

    @GetMapping("/account/{accountId}/available")
    public List<VoucherDTO> getAvailableByAccountId(@PathVariable String accountId) {
        return service.getAvailableVouchersByAccountId(accountId);
    }

    @GetMapping("/public")
    public List<VoucherDTO> getPublicVouchers() {
        return service.getPublicVouchers();
    }

    @GetMapping("/{id}/usable/{accountId}")
    public ResponseEntity<Boolean> isVoucherUsable(@PathVariable String id, @PathVariable String accountId) {
        return ResponseEntity.ok(service.isVoucherUsable(id, accountId));
    }

    @GetMapping("/{id}/used-by/{accountId}")
    public ResponseEntity<Boolean> hasAccountUsedVoucher(@PathVariable String id, @PathVariable String accountId) {
        return ResponseEntity.ok(service.hasAccountUsedVoucher(id, accountId));
    }

}
