package com.pulseras.api.service;

import com.pulseras.api.dto.AccountDto;
import com.pulseras.api.dto.CreateAccountDto;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAll();
    AccountDto getById(String id);
    void create(CreateAccountDto dto);
    void update(String id, CreateAccountDto dto);
    void delete(String id);
}
