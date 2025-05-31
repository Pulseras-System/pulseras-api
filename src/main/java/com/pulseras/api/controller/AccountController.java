package com.pulseras.api.controller;

import com.pulseras.api.dto.AccountDto;
import com.pulseras.api.dto.CreateAccountDto;
import com.pulseras.api.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccountDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AccountDto getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@RequestBody CreateAccountDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody CreateAccountDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}

