package com.pulseras.api.mapper;

import com.pulseras.api.dto.BlogDto;
import com.pulseras.api.entity.Account;
import com.pulseras.api.entity.Blog;

public class BlogMapper {
    public static BlogDto toDto(Blog blog, Account account) {
        BlogDto dto = new BlogDto();
        dto.setBlogId(blog.getBlogId().toHexString());
        dto.setAccountId(blog.getAccountId().toHexString());
        dto.setAccountName(account != null ? account.getFullName() : null);
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setCreateDate(blog.getCreateDate());
        dto.setUpdateDate(blog.getUpdateDate());
        dto.setStatus(blog.getStatus());
        return dto;
    }
}