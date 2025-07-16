package com.pulseras.api.service;

import com.pulseras.api.dto.CreateWishlistDto;
import com.pulseras.api.dto.WishlistDto;

import java.util.Map;

public interface WishlistService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    WishlistDto getById(String id);
    WishlistDto create(CreateWishlistDto dto);
    WishlistDto update(String id, CreateWishlistDto dto);
    void delete(String id);
}
