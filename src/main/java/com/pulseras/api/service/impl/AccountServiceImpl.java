package com.pulseras.api.service.impl;

import com.pulseras.api.dto.*;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.exception.AuthenticationException;
import com.pulseras.api.mapper.AccountMapper;
import com.pulseras.api.entity.Account;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.repository.RoleRepository;
import com.pulseras.api.util.JwtUtil;
import com.pulseras.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public AccountDTO getAccountById(String id) {
        Account acc = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return AccountMapper.toDTO(acc);
    }

    @Override
    public AccountDTO createAccount(CreateAccountDTO dto) {
        if (repository.existsByUsername(dto.getUsername())) {
            throw new AuthenticationException("Username is already taken");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new AuthenticationException("Email is already registered");
        }
        Account entity = AccountMapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setCreateDate(LocalDateTime.now());
        entity.setLastEdited(LocalDateTime.now());
        entity.setRoleId(roleRepository.findByRoleName("Customer").orElseThrow().getId().toString());
        Account saved = repository.save(entity);
        return AccountMapper.toDTO(saved);
    }

    @Override
    public AccountDTO updateAccount(String id, CreateAccountDTO dto) {
        Account existing = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        existing.setFullName(dto.getFullName());
        existing.setUsername(dto.getUsername());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setRoleId(dto.getRoleId());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(LocalDateTime.now());
        // Update password only if present in DTO (optional)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Account updated = repository.save(existing);
        return AccountMapper.toDTO(updated);
    }

    @Override
    public void deleteAccount(String id) {
        ObjectId objId = new ObjectId(id);
        if (!repository.existsById(objId)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        repository.deleteById(objId);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Account account = repository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(account.getUsername());
        AccountDTO accountDTO = AccountMapper.toDTO(account);
        return LoginResponseDTO.builder()
                .token(token)
                .account(accountDTO)
                .build();
    }

    @Override
    public AccountDTO signUp(CreateAccountDTO createAccountDTO) {
        return createAccount(createAccountDTO);
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = repository.findAll();
        return accounts.stream()
                .map(AccountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getRoleByAccountId(String id) {
        Account account = repository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return account.getRoleId();
    }
}
