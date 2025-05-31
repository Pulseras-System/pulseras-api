package com.pulseras.api.service.impl;

import com.pulseras.api.dto.AccountDto;
import com.pulseras.api.dto.CreateAccountDto;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.AccountMapper;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.service.AccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AccountDto> getAll() {
        return repository.findAll().stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getById(String id) {
        var account = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return AccountMapper.toDto(account);
    }

    @Override
    public void create(CreateAccountDto dto) {
        var entity = AccountMapper.toEntity(dto);
        repository.save(entity);
    }

    @Override
    public void update(String id, CreateAccountDto dto) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        existing.setFullName(dto.getFullName());
        existing.setUsername(dto.getUsername());
        existing.setPassword(dto.getPassword());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setRoleId(dto.getRoleId());
        existing.setLastEdited(LocalDateTime.now());

        repository.save(existing);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found");
        }
        repository.deleteById(id);
    }
}
