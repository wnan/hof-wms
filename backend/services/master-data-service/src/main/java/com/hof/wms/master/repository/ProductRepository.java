package com.hof.wms.master.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.master.entity.Product;
import com.hof.wms.master.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final ProductMapper productMapper;

    public IPage<Product> findPage(int pageNum, int pageSize, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getProductName, keyword)
                    .or().like(Product::getSkuCode, keyword)
                    .or().like(Product::getCategoryName, keyword));
        }
        wrapper.orderByDesc(Product::getUpdatedAt);
        return productMapper.selectPage(page, wrapper);
    }

    public Product findById(Long id) {
        return productMapper.selectById(id);
    }

    public Product findBySkuCode(String skuCode) {
        return productMapper.findBySkuCode(skuCode);
    }

    public List<Product> findActiveProducts() {
        return productMapper.findActiveProducts();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.insert(product);
        } else {
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.updateById(product);
        }
        return product;
    }

    public void deleteById(Long id) {
        productMapper.deleteById(id);
    }

    public void batchDelete(List<Long> ids) {
        productMapper.deleteBatchIds(ids);
    }
}
