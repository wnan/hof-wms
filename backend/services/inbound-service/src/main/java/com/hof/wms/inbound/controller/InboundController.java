package com.hof.wms.inbound.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.common.result.ApiResult;
import com.hof.wms.common.result.PageResult;
import com.hof.wms.inbound.entity.InboundOrder;
import com.hof.wms.inbound.entity.InboundOrderItem;
import com.hof.wms.inbound.service.InboundOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inbound")
@RequiredArgsConstructor
public class InboundController {

    private final InboundOrderService orderService;

    @GetMapping("/list")
    public ApiResult<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Page<InboundOrder> page = orderService.getPage(pageNum, pageSize, keyword, status);
        List<Map<String, Object>> rows = page.getRecords().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, page.getTotal(), (long) pageNum, (long) pageSize));
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        InboundOrder order = orderService.getById(id);
        if (order == null) {
            return ApiResult.fail("入库单不存在");
        }
        return ApiResult.success(toMap(order));
    }

    @PostMapping("/save")
    public ApiResult<Map<String, Object>> save(@RequestBody Map<String, Object> command) {
        InboundOrder order = toOrder(command);
        InboundOrder saved;
        if (order.getId() != null) {
            saved = orderService.update(order.getId(), order);
            return ApiResult.success("更新成功", toMap(saved));
        } else {
            saved = orderService.create(order);
            return ApiResult.success("保存成功", toMap(saved));
        }
    }

    @PostMapping("/{id}/submit")
    public ApiResult<Void> submit(@PathVariable Long id) {
        orderService.submit(id);
        return ApiResult.success("已提交审核", null);
    }

    @PostMapping("/{id}/approve")
    public ApiResult<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> command) {
        Boolean pass = (Boolean) command.get("pass");
        if (Boolean.TRUE.equals(pass)) {
            orderService.approve(id);
            return ApiResult.success("审核通过", null);
        } else {
            return ApiResult.success("审核驳回", null);
        }
    }

    @PostMapping("/{id}/confirm")
    public ApiResult<Void> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return ApiResult.success("已确认入库", null);
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> remove(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResult.success("删除成功", null);
    }

    private Map<String, Object> toMap(InboundOrder order) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", order.getId());
        map.put("code", order.getOrderNo());
        map.put("orderNo", order.getOrderNo());
        map.put("supplier", order.getSupplierName());
        map.put("supplierName", order.getSupplierName());
        map.put("type", order.getOrderType());
        map.put("orderType", order.getOrderType());
        map.put("status", order.getStatus());
        map.put("remark", order.getRemark());
        map.put("totalAmount", order.getTotalAmount());
        map.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().toString().replace("T", " ") : null);
        if (order.getItems() != null) {
            map.put("items", order.getItems().stream().map(this::toItemMap).collect(Collectors.toList()));
        } else {
            map.put("items", List.of());
        }
        return map;
    }

    private Map<String, Object> toItemMap(InboundOrderItem item) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", item.getId());
        map.put("skuCode", item.getSkuCode());
        map.put("productName", item.getProductName());
        map.put("quantity", item.getQuantity());
        map.put("unitPrice", item.getUnitPrice());
        map.put("amount", item.getAmount());
        return map;
    }

    private InboundOrder toOrder(Map<String, Object> map) {
        InboundOrder order = new InboundOrder();
        if (map.get("id") != null) {
            order.setId(((Number) map.get("id")).longValue());
        }
        order.setOrderNo((String) map.get("orderNo"));
        order.setSupplierName((String) map.get("supplierName"));
        if (map.get("orderType") != null) {
            order.setOrderType((String) map.get("orderType"));
        } else {
            order.setOrderType("purchase");
        }
        order.setStatus((String) map.get("status"));
        order.setRemark((String) map.get("remark"));
        if (map.get("totalAmount") != null) {
            order.setTotalAmount(new java.math.BigDecimal(map.get("totalAmount").toString()));
        }
        List<?> items = (List<?>) map.get("items");
        if (items != null) {
            order.setItems(items.stream().map(item -> toItem((Map<String, Object>) item)).collect(Collectors.toList()));
        }
        return order;
    }

    private InboundOrderItem toItem(Map<String, Object> map) {
        InboundOrderItem item = new InboundOrderItem();
        if (map.get("id") != null) {
            item.setId(((Number) map.get("id")).longValue());
        }
        item.setSkuCode((String) map.get("skuCode"));
        item.setProductName((String) map.get("productName"));
        if (map.get("quantity") != null) {
            item.setQuantity(new java.math.BigDecimal(map.get("quantity").toString()));
        }
        if (map.get("unitPrice") != null) {
            item.setUnitPrice(new java.math.BigDecimal(map.get("unitPrice").toString()));
        }
        if (map.get("amount") != null) {
            item.setAmount(new java.math.BigDecimal(map.get("amount").toString()));
        }
        return item;
    }
}
