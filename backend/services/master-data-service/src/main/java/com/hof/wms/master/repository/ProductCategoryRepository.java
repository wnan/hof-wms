package com.hof.wms.master.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.master.entity.ProductCategory;
import com.hof.wms.master.mapper.ProductCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCategoryRepository {

    private final ProductCategoryMapper categoryMapper;

    public List<ProductCategory> findAll() {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper();
        wrapper.orderByAsc(ProductCategory::getSort);
        return categoryMapper.selectList(wrapper);
    }

    public List<ProductCategory> findTree() {
        List<ProductCategory> all = findAll();
        return buildTree(all, null);
    }

    private List<ProductCategory> buildTree(List<ProductCategory> all, Long parentId) {
        return all.stream()
                .filter(c -> (parentId == null && c.getParentId() == null) ||
                        (parentId != null && parentId.equals(c.getParentId())))
                .peek(c -> c.setChildren(buildTree(all, c.getId())))
                .toList();
    }

    public ProductCategory findById(Long id) {
        return categoryMapper.selectById(id);
    }

    public ProductCategory save(ProductCategory category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return category;
    }

    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }
}
