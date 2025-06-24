package com.pulseras.api.controller;

import com.pulseras.api.dto.*;
import com.pulseras.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<AccountDTO> signUp(@RequestBody CreateAccountDTO dto) {
        return ResponseEntity.ok(accountService.signUp(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(accountService.login(loginRequest));
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody GoogleLoginRequestDTO request) {
        return ResponseEntity.ok(accountService.googleLogin(request));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody PasswordResetDTO dto) {
        accountService.requestPasswordReset(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO dto) {
        accountService.resetPassword(dto);
        return ResponseEntity.ok().build();
    }
}
