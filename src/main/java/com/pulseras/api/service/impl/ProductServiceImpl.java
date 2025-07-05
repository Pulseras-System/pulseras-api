package com.pulseras.api.service.impl;

import com.pulseras.api.dto.ProductDto;
import com.pulseras.api.dto.CreateProductDto;
import com.pulseras.api.entity.OrderDetail;
import com.pulseras.api.entity.Product;
import com.pulseras.api.entity.Category;
import com.pulseras.api.entity.Promotion;
import com.pulseras.api.exception.ResourceNotFoundException;
import com.pulseras.api.mapper.ProductMapper;
import com.pulseras.api.repository.CategoryRepository;
import com.pulseras.api.repository.OrderDetailRepository;
import com.pulseras.api.repository.ProductRepository;
import com.pulseras.api.repository.PromotionRepository;
import com.pulseras.api.service.ProductService;
import com.pulseras.api.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PromotionRepository promotionRepository;
    private final ProductMapper productMapper;
    private final S3Service s3Service;
    private static final int ACTIVE = 1;


    @Override
    public Map<String, Object> getAll(String keyword, String categoryId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Product> result;

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = categoryId != null && !categoryId.isEmpty();

        if (hasCategory && hasKeyword) {
            result = repository.findByStatusAndCategoryIdsContainingAndProductNameContainingIgnoreCase(ACTIVE, categoryId, keyword, pageable);
        } else if (hasCategory) {
            result = repository.findByStatusAndCategoryIdsContaining(ACTIVE, categoryId, pageable);
        } else if (hasKeyword) {
            result = repository.findByStatusAndProductNameContainingIgnoreCase(ACTIVE, keyword, pageable);
        } else {
            result = repository.findByStatus(ACTIVE, pageable);
        }

        List<ProductDto> content = result.getContent().stream()
                .map(productMapper::toDto)
                .peek(this::applyFinalPrice)
                .toList();

        return Map.of(
                "items", content,
                "totalPages", result.getTotalPages(),
                "totalItems", result.getTotalElements()
        );
    }


    @Override
    public ProductDto getById(String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductDto dto = productMapper.toDto(product);
        applyFinalPrice(dto);
        return dto;
    }

    @Override
    public ProductDto create(CreateProductDto dto, MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                throw new IllegalArgumentException("Hình ảnh là bắt buộc và không được để trống");
            }
            if (!image.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Tệp phải là hình ảnh (ví dụ: .jpg, .png)");
            }

            String imageUrl = s3Service.uploadFile(image);
            Product entity = productMapper.toEntity(dto);
            entity.setProductImage(imageUrl);
            entity.setCreateDate(LocalDateTime.now());
            Product saved = repository.save(entity);
            ProductDto productDto = productMapper.toDto(saved);
            applyFinalPrice(productDto);
            return productDto;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Lỗi xác thực: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo sản phẩm: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductDto update(String id, CreateProductDto dto) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existing.setCategoryIds(dto.getCategoryIds());
        existing.setProductName(dto.getProductName());
        existing.setProductDescription(dto.getProductDescription());
        existing.setProductMaterial(dto.getProductMaterial());
        existing.setQuantity(dto.getQuantity());
        existing.setType(dto.getType());
        existing.setPrice(dto.getPrice());
        existing.setStatus(dto.getStatus());
        existing.setLastEdited(LocalDateTime.now());

        Product updated = repository.save(existing);
        ProductDto productDto = productMapper.toDto(updated);
        applyFinalPrice(productDto);
        return productDto;
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        repository.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> getProductTypeDistribution() {
        List<Product> products = repository.findAll()
                .stream()
                .filter(p -> p.getStatus() != 0)
                .collect(Collectors.toList());

        // Đếm số lượng theo categoryId
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (Product product : products) {
            for (String categoryId : product.getCategoryIds()) {
                categoryCountMap.merge(categoryId, 1, Integer::sum);
            }
        }

        // Map từ categoryId -> categoryName
        Map<String, String> categoryNameMap = categoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getCategoryName));

        // Trả về list map {"label": categoryName, "value": count}
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            String categoryId = entry.getKey();
            Integer count = entry.getValue();
            String name = categoryNameMap.getOrDefault(categoryId, "Unknown");

            Map<String, Object> item = new HashMap<>();
            item.put("label", name);
            item.put("value", count);
            result.add(item);
        }

        return result;
    }

    @Override
    public List<ProductDto> getTopBuyProducts() {

        // Gom nhóm OrderDetail hợp lệ (status != 0) thành bảng đếm
        Map<String, Long> productCount = orderDetailRepository.findAll().stream()
                .filter(od -> od.getStatus() != null && od.getStatus() != 0)
                .filter(od -> od.getProductId() != null)
                .collect(Collectors.groupingBy(
                        OrderDetail::getProductId,
                        Collectors.counting()
                ));

        return productCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(6)
                .map(entry -> repository.findById(entry.getKey())
                        .filter(p -> p.getStatus() == ACTIVE)
                        .map(productMapper::toDto)
                        .map(dto -> {
                            applyFinalPrice(dto);
                            return dto;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /** TOP 6 sản phẩm mới nhất */
    @Override
    public List<ProductDto> getLatestProducts() {
        List<Product> products = repository
                .findTop6ByStatusOrderByCreateDateDesc(ACTIVE);

        return products.stream()
                .map(productMapper::toDto)
                .peek(this::applyFinalPrice)
                .collect(Collectors.toList());
    }


    private void applyFinalPrice(ProductDto dto) {
        Optional<Promotion> promo = promotionRepository
                .findFirstByProductIdAndStatusOrderByCreateDateDesc(dto.getProductId(), ACTIVE);
        if (promo.isPresent()) {
            BigDecimal discount = dto.getPrice()
                    .multiply(BigDecimal.valueOf(promo.get().getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100));
            dto.setFinalPrice(dto.getPrice().subtract(discount));
        } else {
            dto.setFinalPrice(null);
        }
    }

}
