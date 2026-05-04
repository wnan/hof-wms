package com.hof.wms.master.service;

import com.hof.wms.master.entity.ProductCategory;
import com.hof.wms.master.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public List<ProductCategory> getTree() {
        return categoryRepository.findTree();
    }

    public ProductCategory getById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public ProductCategory create(ProductCategory category) {
        category.setId(null);
        return categoryRepository.save(category);
    }

    @Transactional
    public ProductCategory update(Long id, ProductCategory category) {
        ProductCategory existing = categoryRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("分类不存在");
        }
        category.setId(id);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
