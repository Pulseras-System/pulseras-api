package com.pulseras.api.service;

import com.pulseras.api.dto.*;

import java.util.List;
import java.util.Map;

public interface AccountService {
    AccountDTO getAccountById(String id);
    AccountDTO createAccount(CreateAccountDTO dto);
    AccountDTO updateAccount(String id, CreateAccountDTO dto);
    void deleteAccount(String id);
    List<AccountDTO> getAllAccounts();
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    AccountDTO signUp(CreateAccountDTO createAccountDTO);
    String getRoleByAccountId(String id);
    LoginResponseDTO googleLogin(GoogleLoginRequestDTO request);
    Map<String, Object> totalCustomersWithCompare();
    void requestPasswordReset(PasswordResetDTO dto);
    void resetPassword(ResetPasswordDTO dto);
    List<AccountDTO> getAccountsByRole(String role);
    int countOrdersByAccountId(String accountId);
    int countTotalSpent(String accountId);
}
