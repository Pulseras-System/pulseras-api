package com.pulseras.api.service;

import com.pulseras.api.dto.*;

public interface AccountService {
    AccountDTO getAccountById(String id);
    AccountDTO createAccount(CreateAccountDTO dto);
    AccountDTO updateAccount(String id, CreateAccountDTO dto);
    void deleteAccount(String id);

    LoginResponseDTO login(LoginRequestDTO loginRequest);
    AccountDTO signUp(CreateAccountDTO createAccountDTO);
}
