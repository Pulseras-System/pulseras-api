package com.pulseras.api.service.impl;

import com.pulseras.api.dto.CreateWishlistDto;
import com.pulseras.api.dto.WishlistDto;
import com.pulseras.api.entity.Wishlist;
import com.pulseras.api.entity.Product;
import com.pulseras.api.entity.Account;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.WishlistMapper;
import com.pulseras.api.repository.WishlistRepository;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.repository.ProductRepository;
import com.pulseras.api.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Override
    public Map<String, Object> getAll(String keyword, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Wishlist> pageResult = wishlistRepository.findAll(pageable);

        List<WishlistDto> all = pageResult.getContent().stream().map(w -> {
            Account acc = accountRepository.findById(w.getAccountId()).orElse(null);
            Product prod = productRepository.findById(w.getProductId()).orElse(null);
            return WishlistMapper.toDto(w, acc, prod);
        }).toList();

        List<WishlistDto> filtered = (keyword != null && !keyword.isBlank())
                ? all.stream().filter(w ->
                (w.getFullName() != null && w.getFullName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (w.getProductName() != null && w.getProductName().toLowerCase().contains(keyword.toLowerCase()))
        ).collect(Collectors.toList())
                : all;

        int totalItems = filtered.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int from = Math.min(page * size, totalItems);
        int to = Math.min(from + size, totalItems);

        return Map.of(
                "items", filtered.subList(from, to),
                "totalPages", totalPages,
                "totalItems", totalItems
        );
    }


    @Override
    public WishlistDto getById(String id) {
        Wishlist w = wishlistRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        Account acc = accountRepository.findById(w.getAccountId()).orElse(null);
        Product prod = productRepository.findById(w.getProductId()).orElse(null);

        return WishlistMapper.toDto(w, acc, prod);
    }

    @Override
    public WishlistDto create(CreateWishlistDto dto) {
        Wishlist w = WishlistMapper.toEntity(dto);
        w.setAccountId(new ObjectId(dto.getAccountId()));
        w.setCreateDate(LocalDateTime.now());

        Wishlist saved = wishlistRepository.save(w);

        Account acc = accountRepository.findById(saved.getAccountId()).orElse(null);
        Product prod = productRepository.findById(saved.getProductId()).orElse(null);

        return WishlistMapper.toDto(saved, acc, prod);
    }

    @Override
    public WishlistDto update(String id, CreateWishlistDto dto) {
        Wishlist w = wishlistRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        w.setAccountId(new ObjectId(dto.getAccountId()));
        w.setProductId(dto.getProductId()); // string as-is
        w.setStatus(dto.getStatus());
        w.setLastEdited(LocalDateTime.now());

        Wishlist updated = wishlistRepository.save(w);

        Account acc = accountRepository.findById(updated.getAccountId()).orElse(null);
        Product prod = productRepository.findById(updated.getProductId()).orElse(null);

        return WishlistMapper.toDto(updated, acc, prod);
    }

    @Override
    public void delete(String id) {
        ObjectId objId = new ObjectId(id);
        if (!wishlistRepository.existsById(objId)) {
            throw new ResourceNotFoundException("Wishlist not found");
        }
        wishlistRepository.deleteById(objId);
    }
}
