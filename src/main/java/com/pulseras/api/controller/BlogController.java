package com.pulseras.api.controller;

import com.pulseras.api.dto.BlogDto;
import com.pulseras.api.dto.CreateBlogDto;
import com.pulseras.api.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService service;

    @GetMapping
    public Map<String, Object> getAll(@RequestParam(defaultValue = "") String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "createDate") String sort) {
        return service.getAll(keyword, page, size, sort);
    }

    @GetMapping("/account/{accountId}")
    public List<BlogDto> getByAccountId(@PathVariable String accountId) {
        return service.getByAccountId(accountId);
    }

    @GetMapping("/newest")
    public List<BlogDto> get5Newest() {
        return service.get5NewestBlogs();
    }

    @PostMapping
    public BlogDto create(@RequestBody CreateBlogDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public BlogDto update(@PathVariable String id, @RequestBody CreateBlogDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
