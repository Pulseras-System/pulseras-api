package com.pulseras.api.service;

import com.pulseras.api.dto.*;

import java.util.List;

public interface AccountService {
    AccountDTO getAccountById(String id);
    AccountDTO createAccount(CreateAccountDTO dto);
    AccountDTO updateAccount(String id, CreateAccountDTO dto);
    void deleteAccount(String id);
    List<AccountDTO> getAllAccounts();
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    AccountDTO signUp(CreateAccountDTO createAccountDTO);
}
