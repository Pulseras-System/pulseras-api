package com.pulseras.api.controller;

import com.pulseras.api.dto.*;
import com.pulseras.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> update(@PathVariable String id, @RequestBody CreateAccountDTO dto) {
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<String> getRoleByAccountId(@PathVariable String id) {
        String roleId = accountService.getRoleByAccountId(id);
        return ResponseEntity.ok(roleId);
    }

    @GetMapping("/total-customers")
    public ResponseEntity<Map<String, Object>> totalCustomers() {
        return ResponseEntity.ok(accountService.totalCustomersWithCompare());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<AccountDTO>> getByRole(@RequestParam String role) {
        return ResponseEntity.ok(accountService.getAccountsByRole(role));
    }

    @GetMapping("count/orders")
    public ResponseEntity<Integer> totalOrders(@RequestParam String id) {
        return ResponseEntity.ok(accountService.countOrdersByAccountId(id));
    }

    @GetMapping("count/total-spent")
    public ResponseEntity<Integer> totalSpent(@RequestParam String id) {
        return ResponseEntity.ok(accountService.countTotalSpent(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountDTO> partialUpdate(@PathVariable String id, @RequestBody UpdateAccountDTO dto) {
        return ResponseEntity.ok(accountService.partialUpdateAccount(id, dto));
    }

}
