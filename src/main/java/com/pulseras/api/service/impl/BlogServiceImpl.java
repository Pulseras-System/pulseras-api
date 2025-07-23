package com.pulseras.api.service.impl;

import com.pulseras.api.dto.BlogDto;
import com.pulseras.api.dto.CreateBlogDto;
import com.pulseras.api.entity.Blog;
import com.pulseras.api.entity.Account;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.BlogMapper;
import com.pulseras.api.repository.BlogRepository;
import com.pulseras.api.repository.AccountRepository;
import com.pulseras.api.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepo;
    private final AccountRepository accRepo;

    @Override
    public Map<String, Object> getAll(String keyword, int page, int size, String sort) {
        List<Blog> all = blogRepo.findAll();
        List<BlogDto> filtered = all.stream()
                .filter(b -> {
                    if (keyword == null || keyword.isBlank()) return true;
                    String low = keyword.toLowerCase();
                    return b.getTitle().toLowerCase().contains(low)
                            || b.getContent().toLowerCase().contains(low);
                })
                .sorted(Comparator.comparing(Blog::getCreateDate).reversed())
                .map(b -> BlogMapper.toDto(
                        b, accRepo.findById(b.getAccountId()).orElse(null)))
                .collect(Collectors.toList());

        int from = Math.min(page * size, filtered.size());
        int to = Math.min(from + size, filtered.size());
        int totalPages = (int) Math.ceil((double) filtered.size() / size);

        return Map.of(
                "items", filtered.subList(from, to),
                "totalPages", totalPages,
                "totalItems", filtered.size()
        );
    }

    @Override
    public List<BlogDto> getByAccountId(String accountId) {
        ObjectId accId = new ObjectId(accountId);
        Account acc = accRepo.findById(accId).orElse(null);
        return blogRepo.findByAccountId(accId).stream()
                .map(b -> BlogMapper.toDto(b, acc))
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogDto> get5NewestBlogs() {
        return blogRepo.findTop5ByStatusOrderByCreateDateDesc(1).stream()
                .map(b -> BlogMapper.toDto(b, accRepo.findById(b.getAccountId()).orElse(null)))
                .collect(Collectors.toList());
    }

    @Override
    public BlogDto create(CreateBlogDto dto) {
        ObjectId accId = new ObjectId(dto.getAccountId());
        Blog b = Blog.builder()
                .accountId(accId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(dto.getStatus())
                .createDate(LocalDateTime.now())
                .build();
        Blog saved = blogRepo.save(b);
        return BlogMapper.toDto(saved, accRepo.findById(accId).orElse(null));
    }

    @Override
    public BlogDto update(String id, CreateBlogDto dto) {
        ObjectId oid = new ObjectId(id);
        Blog b = blogRepo.findById(oid)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
        b.setTitle(dto.getTitle());
        b.setContent(dto.getContent());
        b.setStatus(dto.getStatus());
        b.setUpdateDate(LocalDateTime.now());
        Blog updated = blogRepo.save(b);
        ObjectId accId = new ObjectId(dto.getAccountId());
        return BlogMapper.toDto(updated, accRepo.findById(accId).orElse(null));
    }

    @Override
    public void delete(String id) {
        ObjectId oid = new ObjectId(id);
        if (!blogRepo.existsById(oid)) {
            throw new ResourceNotFoundException("Blog not found");
        }
        blogRepo.deleteById(oid);
    }
}
