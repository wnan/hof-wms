package com.hof.wms.master.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hof.wms.master.entity.Product;
import com.hof.wms.master.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public IPage<Product> getPage(int pageNum, int pageSize, String keyword) {
        return productRepository.findPage(pageNum, pageSize, keyword);
    }

    public Product getById(Long id) {
        return productRepository.findById(id);
    }

    public Product getBySkuCode(String skuCode) {
        return productRepository.findBySkuCode(skuCode);
    }

    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    @Transactional
    public Product create(Product product) {
        product.setId(null);
        product.setStatus("on");
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setId(id);
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void batchDelete(List<Long> ids) {
        productRepository.batchDelete(ids);
    }

    @Transactional
    public void toggleStatus(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus("on".equals(product.getStatus()) ? "off" : "on");
        productRepository.save(product);
    }
}
