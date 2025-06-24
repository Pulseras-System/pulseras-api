package com.pulseras.api.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.pulseras.api.dto.*;
import com.pulseras.api.entity.PasswordResetToken;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.exception.AuthenticationException;
import com.pulseras.api.mapper.AccountMapper;
import com.pulseras.api.entity.Account;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.repository.PasswordResetTokenRepository;
import com.pulseras.api.repository.RoleRepository;
import com.pulseras.api.service.EmailService;
import com.pulseras.api.util.GoogleTokenVerifier;
import com.pulseras.api.util.JwtUtil;
import com.pulseras.api.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    @Value("${reset.token.expiry}")
    private int tokenExpiryMinutes;

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

    @Override
    public LoginResponseDTO googleLogin(GoogleLoginRequestDTO request) {
        try {
            // 1. Verify token bằng Firebase
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());

            String email = firebaseToken.getEmail();
            String name = (String) firebaseToken.getClaims().get("name");
//            String sub = payload.getSubject();

            // Try to find account by email
            Account account = repository.findByEmail(email).orElse(null);

            if (account == null) {
                // Auto create new account
                account = Account.builder()
                        .fullName(name)
                        .email(email)
                        .username(email)
                        .password(passwordEncoder.encode(email))
                        .roleId(roleRepository.findByRoleName("Customer").orElseThrow().getId().toString())
                        .status(1)
                        .createDate(LocalDateTime.now())
                        .lastEdited(LocalDateTime.now())
                        .build();
                account = repository.save(account);
            }

            String token = jwtUtil.generateToken(account.getUsername());

            return LoginResponseDTO.builder()
                    .token(token)
                    .account(AccountMapper.toDTO(account))
                    .build();

        } catch (Exception e) {
            throw new AuthenticationException("Google login failed: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> totalCustomersWithCompare() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        LocalDateTime startThisWeek = startOfThisWeek.atStartOfDay();
        LocalDateTime startLastWeek = startOfLastWeek.atStartOfDay();
        LocalDateTime endLastWeek = endOfLastWeek.atTime(LocalTime.MAX);

        // ✅ Tổng số khách hàng (role = 1)
        long totalCustomers = repository.findAll()
                .stream()
                .filter(acc -> acc.getRoleId().equals(roleRepository.findByRoleName("Customer").orElseThrow().getId().toString()))
                .count();

        // ✅ Tuần này
        long thisWeekCustomers = repository.findByCreateDateBetween(startThisWeek, LocalDateTime.now())
                .stream()
                .filter(acc -> acc.getRoleId().equals(roleRepository.findByRoleName("Customer").orElseThrow().getId().toString()))
                .count();

        // ✅ Tuần trước
        long lastWeekCustomers = repository.findByCreateDateBetween(startLastWeek, endLastWeek)
                .stream()
                .filter(acc -> acc.getRoleId().equals(roleRepository.findByRoleName("Customer").orElseThrow().getId().toString()))
                .count();

        // ✅ Tính phần trăm thay đổi
        double percentChange = 0;
        if (lastWeekCustomers > 0) {
            percentChange = ((double) (thisWeekCustomers - lastWeekCustomers) / lastWeekCustomers) * 100;
        } else if (thisWeekCustomers > 0) {
            percentChange = 100;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCustomers", totalCustomers);
        result.put("thisWeekCustomers", thisWeekCustomers);
        result.put("lastWeekCustomers", lastWeekCustomers);
        result.put("percentChange", percentChange);
        result.put("isIncrease", thisWeekCustomers >= lastWeekCustomers);

        return result;
    }

    @Override
    public void requestPasswordReset(PasswordResetDTO dto) {
        Account account = repository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with email: " + dto.getEmail()));

        passwordResetTokenRepository.deleteByAccountId(account.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .accountId(account.getId())
                .expiryDate(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send email with reset link
        try {
            emailService.sendPasswordResetEmail(account.getEmail(), token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new AuthenticationException("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new AuthenticationException("Reset token has expired");
        }

        Account account = repository.findById(resetToken.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // Update password
        account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        account.setLastEdited(LocalDateTime.now());
        repository.save(account);

        // Delete the used token
        passwordResetTokenRepository.delete(resetToken);
    }


}
