package com.hof.wms.master.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hof.wms.common.result.ApiResult;
import com.hof.wms.common.result.PageResult;
import com.hof.wms.master.entity.Product;
import com.hof.wms.master.entity.ProductCategory;
import com.hof.wms.master.service.ProductCategoryService;
import com.hof.wms.master.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuController {

    private final ProductService productService;
    private final ProductCategoryService categoryService;

    @GetMapping("/list")
    public ApiResult<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        IPage<Product> page = productService.getPage(pageNum, pageSize, keyword);
        List<Map<String, Object>> rows = page.getRecords().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, page.getTotal(), (long) pageNum, (long) pageSize));
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return ApiResult.fail("商品不存在");
        }
        return ApiResult.success(toMap(product));
    }

    @PostMapping
    public ApiResult<Map<String, Object>> create(@RequestBody Map<String, Object> command) {
        Product product = toProduct(command);
        Product saved = productService.create(product);
        return ApiResult.success("创建成功", toMap(saved));
    }

    @PutMapping("/{id}")
    public ApiResult<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> command) {
        Product product = toProduct(command);
        Product updated = productService.update(id, product);
        return ApiResult.success("更新成功", toMap(updated));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> remove(@PathVariable Long id) {
        productService.delete(id);
        return ApiResult.success("删除成功", null);
    }

    @PostMapping("/{id}/status")
    public ApiResult<Void> toggleStatus(@PathVariable Long id) {
        productService.toggleStatus(id);
        return ApiResult.success("状态已更新", null);
    }

    @PostMapping("/batch-remove")
    public ApiResult<Void> batchRemove(@RequestBody Map<String, Object> command) {
        List<?> ids = (List<?>) command.get("ids");
        if (ids != null) {
            List<Long> idList = ids.stream().map(id -> {
                if (id instanceof Number) {
                    return ((Number) id).longValue();
                }
                return Long.parseLong(id.toString());
            }).collect(Collectors.toList());
            productService.batchDelete(idList);
        }
        return ApiResult.success("删除成功", null);
    }

    @PostMapping("/import")
    public ApiResult<Map<String, Object>> importSku(@RequestBody Object command) {
        return ApiResult.success(Map.of("success", 0, "fail", 0));
    }

    @GetMapping("/category/tree")
    public ApiResult<List<Map<String, Object>>> categoryTree() {
        List<ProductCategory> tree = categoryService.getTree();
        return ApiResult.success(tree.stream().map(this::toCategoryMap).collect(Collectors.toList()));
    }

    @PostMapping("/category")
    public ApiResult<Map<String, Object>> categoryCreate(@RequestBody Map<String, Object> command) {
        ProductCategory category = toCategory(command);
        ProductCategory saved = categoryService.create(category);
        return ApiResult.success("创建成功", toCategoryMap(saved));
    }

    @PutMapping("/category/{id}")
    public ApiResult<Map<String, Object>> categoryUpdate(@PathVariable Long id, @RequestBody Map<String, Object> command) {
        ProductCategory category = toCategory(command);
        ProductCategory updated = categoryService.update(id, category);
        return ApiResult.success("更新成功", toCategoryMap(updated));
    }

    @DeleteMapping("/category/{id}")
    public ApiResult<Void> categoryDelete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResult.success("删除成功", null);
    }

    private Map<String, Object> toMap(Product product) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", product.getId());
        map.put("skuCode", product.getSkuCode());
        map.put("skuName", product.getProductName());
        map.put("category", product.getCategoryName());
        map.put("brand", product.getBrand());
        map.put("unit", product.getUnit());
        map.put("spec", product.getSpec());
        map.put("barcode", product.getBarcode());
        map.put("costPrice", product.getCostPrice());
        map.put("salePrice", product.getSalePrice());
        map.put("weight", product.getWeight());
        map.put("volume", product.getVolume());
        map.put("supplier", product.getSupplierName());
        map.put("safetyStock", product.getSafetyStock());
        map.put("status", product.getStatus());
        map.put("remark", product.getRemark());
        map.put("createdAt", product.getCreatedAt() != null ? product.getCreatedAt().toString().replace("T", " ") : null);
        map.put("updatedAt", product.getUpdatedAt() != null ? product.getUpdatedAt().toString().replace("T", " ") : null);
        return map;
    }

    private Product toProduct(Map<String, Object> map) {
        Product product = new Product();
        if (map.get("id") != null) {
            product.setId(((Number) map.get("id")).longValue());
        }
        product.setSkuCode((String) map.get("skuCode"));
        product.setProductName((String) map.get("skuName"));
        product.setCategoryName((String) map.get("category"));
        product.setBrand((String) map.get("brand"));
        product.setUnit((String) map.get("unit"));
        product.setSpec((String) map.get("spec"));
        product.setBarcode((String) map.get("barcode"));
        if (map.get("costPrice") != null) {
            product.setCostPrice(new java.math.BigDecimal(map.get("costPrice").toString()));
        }
        if (map.get("salePrice") != null) {
            product.setSalePrice(new java.math.BigDecimal(map.get("salePrice").toString()));
        }
        if (map.get("weight") != null) {
            product.setWeight(new java.math.BigDecimal(map.get("weight").toString()));
        }
        if (map.get("volume") != null) {
            product.setVolume(new java.math.BigDecimal(map.get("volume").toString()));
        }
        product.setSupplierName((String) map.get("supplier"));
        if (map.get("safetyStock") != null) {
            product.setSafetyStock(new java.math.BigDecimal(map.get("safetyStock").toString()));
        }
        product.setStatus((String) map.get("status"));
        product.setRemark((String) map.get("remark"));
        return product;
    }

    private Map<String, Object> toCategoryMap(ProductCategory category) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("parentId", category.getParentId());
        map.put("sort", category.getSort());
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            map.put("children", category.getChildren().stream()
                    .map(this::toCategoryMap)
                    .collect(Collectors.toList()));
        }
        return map;
    }

    private ProductCategory toCategory(Map<String, Object> map) {
        ProductCategory category = new ProductCategory();
        if (map.get("id") != null) {
            category.setId(((Number) map.get("id")).longValue());
        }
        if (map.get("parentId") != null) {
            category.setParentId(((Number) map.get("parentId")).longValue());
        }
        category.setName((String) map.get("name"));
        if (map.get("sort") != null) {
            category.setSort(((Number) map.get("sort")).intValue());
        }
        return category;
    }
}
