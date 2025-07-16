package com.pulseras.api.controller;

import com.pulseras.api.dto.*;
import com.pulseras.api.service.AccountService;
import com.pulseras.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final EmailService emailService;

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

    @GetMapping("/total-customers")
    public ResponseEntity<Map<String, Object>> totalCustomers() {
        return ResponseEntity.ok(accountService.totalCustomersWithCompare());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<AccountDTO>> getByRole(@RequestParam String role) {
        return ResponseEntity.ok(accountService.getAccountsByRole(role));
    }

    @GetMapping("count/orders")
    public ResponseEntity<Integer> totalOrders(@RequestParam String id) {
        return ResponseEntity.ok(accountService.countOrdersByAccountId(id));
    }

    @GetMapping("count/total-spent")
    public ResponseEntity<Integer> totalSpent(@RequestParam String id) {
        return ResponseEntity.ok(accountService.countTotalSpent(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountDTO> partialUpdate(@PathVariable String id, @RequestBody UpdateAccountDTO dto) {
        return ResponseEntity.ok(accountService.partialUpdateAccount(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable String id, @RequestBody StatusUpdateDTO statusUpdate) {
        try {
            AccountDTO account = accountService.getAccountById(id);
            
            // Update status
            UpdateAccountDTO updateDto = new UpdateAccountDTO();
            updateDto.setStatus(statusUpdate.getStatus());
            accountService.partialUpdateAccount(id, updateDto);
            
            // Send email notification if account is being deactivated/reactivated
            String message;
            if (statusUpdate.getStatus() == 0) {
                message = "Tài khoản của bạn đã bị tạm khóa.";
                if (statusUpdate.getReason() != null && !statusUpdate.getReason().trim().isEmpty()) {
                    message += " Lý do: " + statusUpdate.getReason();
                }
            } else if (statusUpdate.getStatus() == 1) {
                message = "Tài khoản của bạn đã được kích hoạt lại.";
            } else {
                message = "Trạng thái tài khoản của bạn đã được cập nhật.";
            }
            
            // Send notification email
            try {
                emailService.sendEmail(account.getEmail(), message, "ACC-" + id);
            } catch (Exception e) {
                // Log error but don't fail the status update
                System.err.println("Failed to send email notification: " + e.getMessage());
            }
            
            return ResponseEntity.ok("Cập nhật trạng thái tài khoản thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

}
