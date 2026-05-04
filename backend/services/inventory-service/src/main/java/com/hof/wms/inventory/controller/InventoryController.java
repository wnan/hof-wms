package com.hof.wms.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.common.result.ApiResult;
import com.hof.wms.common.result.PageResult;
import com.hof.wms.inventory.entity.Inventory;
import com.hof.wms.inventory.entity.InventoryCheckItem;
import com.hof.wms.inventory.entity.InventoryCheckOrder;
import com.hof.wms.inventory.service.InventoryCheckService;
import com.hof.wms.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryCheckService checkService;

    @GetMapping("/inventory/list")
    public ApiResult<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Inventory> page = inventoryService.getPage(pageNum, pageSize, keyword);
        List<Map<String, Object>> rows = page.getRecords().stream()
                .map(this::toInventoryMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, page.getTotal(), (long) pageNum, (long) pageSize));
    }

    @PostMapping("/inventory/check/save")
    public ApiResult<Map<String, Object>> checkSave(@RequestBody Map<String, Object> command) {
        InventoryCheckOrder order = toCheckOrder(command);
        InventoryCheckOrder saved;
        if (order.getId() != null) {
            saved = checkService.update(order.getId(), order);
            return ApiResult.success("更新成功", toCheckOrderMap(saved));
        } else {
            saved = checkService.create(order);
            return ApiResult.success("保存成功", toCheckOrderMap(saved));
        }
    }

    @PostMapping("/inventory/check/{id}/submit")
    public ApiResult<Void> checkSubmit(@PathVariable Long id) {
        checkService.submit(id);
        return ApiResult.success("盘点已提交", null);
    }

    @GetMapping("/inventory/alert")
    public ApiResult<PageResult<Map<String, Object>>> alertList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Inventory> alertItems = inventoryService.getAlertList();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, alertItems.size());
        List<Map<String, Object>> rows = alertItems.subList(start, Math.min(end, alertItems.size())).stream()
                .map(this::toAlertMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, (long) alertItems.size(), (long) pageNum, (long) pageSize));
    }

    @PostMapping("/inventory/alert/set")
    public ApiResult<Void> alertSet(@RequestBody Map<String, Object> command) {
        Long id = ((Number) command.get("id")).longValue();
        BigDecimal safetyStock = new BigDecimal(command.get("safetyStock").toString());
        inventoryService.adjustSafetyStock(id, safetyStock);
        return ApiResult.success("阈值已更新", null);
    }

    @GetMapping("/dashboard/summary")
    public ApiResult<Map<String, Object>> dashboard() {
        Map<String, Object> summary = inventoryService.getDashboardSummary();
        return ApiResult.success(Map.of(
                "metrics", Map.of(
                        "inboundCount", 0,
                        "outboundCount", 0,
                        "inventorySkuCount", summary.get("totalInventoryCount"),
                        "alertCount", summary.get("alertCount")
                ),
                "latestInbound", List.of(),
                "trend", List.of(),
                "categoryDist", List.of(),
                "warehouseLoad", List.of()
        ));
    }

    private Map<String, Object> toInventoryMap(Inventory inventory) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", inventory.getId());
        map.put("skuCode", inventory.getSkuCode());
        map.put("skuName", inventory.getProductName());
        map.put("productName", inventory.getProductName());
        map.put("category", inventory.getCategoryName());
        map.put("warehouse", inventory.getWarehouseName());
        map.put("warehouseName", inventory.getWarehouseName());
        map.put("availableQty", inventory.getAvailableQty());
        map.put("stock", inventory.getAvailableQty());
        map.put("lockedQty", inventory.getLockedQty());
        map.put("totalQty", inventory.getTotalQty());
        map.put("safetyStock", inventory.getSafetyStock());
        map.put("updatedAt", inventory.getUpdatedAt() != null ? inventory.getUpdatedAt().toString().replace("T", " ") : null);
        String status = inventory.getAvailableQty().compareTo(inventory.getSafetyStock()) < 0 ? "low" : "normal";
        map.put("status", status);
        return map;
    }

    private Map<String, Object> toAlertMap(Inventory inventory) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", inventory.getId());
        map.put("skuCode", inventory.getSkuCode());
        map.put("skuName", inventory.getProductName());
        map.put("productName", inventory.getProductName());
        map.put("stock", inventory.getAvailableQty());
        map.put("availableQty", inventory.getAvailableQty());
        map.put("safetyStock", inventory.getSafetyStock());
        map.put("warehouse", inventory.getWarehouseName());
        map.put("alertType", "low");
        return map;
    }

    private Map<String, Object> toCheckOrderMap(InventoryCheckOrder order) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", order.getId());
        map.put("checkNo", order.getCheckNo());
        map.put("warehouseName", order.getWarehouseName());
        map.put("status", order.getStatus());
        map.put("checkDate", order.getCheckDate() != null ? order.getCheckDate().toString() : null);
        map.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString().replace("T", " ") : null);
        if (order.getItems() != null) {
            map.put("items", order.getItems().stream().map(this::toCheckItemMap).collect(Collectors.toList()));
        } else {
            map.put("items", List.of());
        }
        return map;
    }

    private Map<String, Object> toCheckItemMap(InventoryCheckItem item) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", item.getId());
        map.put("skuCode", item.getSkuCode());
        map.put("productName", item.getProductName());
        map.put("systemQty", item.getSystemQty());
        map.put("actualQty", item.getActualQty());
        map.put("diffQty", item.getDiffQty());
        return map;
    }

    private InventoryCheckOrder toCheckOrder(Map<String, Object> map) {
        InventoryCheckOrder order = new InventoryCheckOrder();
        if (map.get("id") != null) {
            order.setId(((Number) map.get("id")).longValue());
        }
        order.setWarehouseName((String) map.get("warehouseName"));
        order.setStatus((String) map.get("status"));
        List<?> items = (List<?>) map.get("items");
        if (items != null) {
            order.setItems(items.stream().map(item -> toCheckItem((Map<String, Object>) item)).collect(Collectors.toList()));
        }
        return order;
    }

    private InventoryCheckItem toCheckItem(Map<String, Object> map) {
        InventoryCheckItem item = new InventoryCheckItem();
        if (map.get("id") != null) {
            item.setId(((Number) map.get("id")).longValue());
        }
        item.setSkuCode((String) map.get("skuCode"));
        item.setProductName((String) map.get("productName"));
        if (map.get("systemQty") != null) {
            item.setSystemQty(new BigDecimal(map.get("systemQty").toString()));
        }
        if (map.get("actualQty") != null) {
            item.setActualQty(new BigDecimal(map.get("actualQty").toString()));
        }
        if (map.get("diffQty") != null) {
            item.setDiffQty(new BigDecimal(map.get("diffQty").toString()));
        }
        return item;
    }
}
