package com.pulseras.api.mapper;

import com.pulseras.api.dto.AccountDto;
import com.pulseras.api.dto.CreateAccountDto;
import com.pulseras.api.entity.Account;
import com.pulseras.api.util.PasswordUtil;

public class AccountMapper {
    public static AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setAccountId(account.getAccountId());
        dto.setFullName(account.getFullName());
        dto.setUsername(account.getUsername());
        dto.setPhone(account.getPhone());
        dto.setEmail(account.getEmail());
        dto.setRoleId(account.getRoleId());
        dto.setStatus(account.getStatus());
        return dto;
    }

    public static Account toEntity(CreateAccountDto dto) {
        Account account = new Account();
        account.setFullName(dto.getFullName());
        account.setPassword(PasswordUtil.hash(dto.getPassword()));
        account.setUsername(dto.getUsername());
        account.setPhone(dto.getPhone());
        account.setEmail(dto.getEmail());
        account.setRoleId(dto.getRoleId());
        return account;
    }
}
