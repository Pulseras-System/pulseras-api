package com.pulseras.api.controller;

import com.pulseras.api.dto.*;
import com.pulseras.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<AccountDTO> signUp(@RequestBody CreateAccountDTO dto) {
        return ResponseEntity.ok(accountService.signUp(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(accountService.login(loginRequest));
    }

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
}
