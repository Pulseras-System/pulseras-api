package com.pulseras.api.service;

import com.pulseras.api.dto.CreateWishlistDto;
import com.pulseras.api.dto.WishlistDto;
import org.bson.types.ObjectId;

import java.util.Map;
import java.util.List;

public interface WishlistService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    WishlistDto getById(String id);
    WishlistDto create(CreateWishlistDto dto);
    WishlistDto update(String id, CreateWishlistDto dto);
    void delete(String id);
    List<WishlistDto> getByAccountId(String accountId);
}
