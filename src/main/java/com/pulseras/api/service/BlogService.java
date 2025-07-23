package com.pulseras.api.service;

import com.pulseras.api.dto.BlogDto;
import com.pulseras.api.dto.CreateBlogDto;
import java.util.List;
import java.util.Map;

public interface BlogService {
    Map<String, Object> getAll(String keyword, int page, int size, String sort);
    List<BlogDto> getByAccountId(String accountId);
    BlogDto getById(String id);
    List<BlogDto> get5NewestBlogs();
    BlogDto create(CreateBlogDto dto);
    BlogDto update(String id, CreateBlogDto dto);
    void delete(String id);
}
