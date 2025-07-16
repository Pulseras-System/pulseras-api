package com.pulseras.api.mapper;

import com.pulseras.api.dto.CreateWishlistDto;
import com.pulseras.api.dto.WishlistDto;
import com.pulseras.api.entity.Account;
import com.pulseras.api.entity.Product;
import com.pulseras.api.entity.Wishlist;
import org.bson.types.ObjectId;

public class WishlistMapper {

    public static Wishlist toEntity(CreateWishlistDto dto) {
        return Wishlist.builder()
                .accountId(new ObjectId(dto.getAccountId()))
                .productId(dto.getProductId())
                .status(dto.getStatus())
                .build();
    }

    public static WishlistDto toDto(Wishlist w, Account acc, Product prod) {
        return WishlistDto.builder()
                .wishlistId(w.getWishlistId().toHexString())
                .accountId(w.getAccountId().toHexString())
                .productId(w.getProductId())
                .productName(prod != null ? prod.getProductName() : null)
                .fullName(acc != null ? acc.getFullName() : null)
                .status(w.getStatus())
                .createDate(w.getCreateDate())
                .lastEdited(w.getLastEdited())
                .build();
    }
}
