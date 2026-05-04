package com.hof.wms.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.inventory.entity.Inventory;
import com.hof.wms.inventory.entity.InventoryCheckItem;
import com.hof.wms.inventory.entity.InventoryCheckOrder;
import com.hof.wms.inventory.repository.InventoryCheckRepository;
import com.hof.wms.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryCheckService {

    private final InventoryCheckRepository checkRepository;
    private final InventoryRepository inventoryRepository;

    public Page<InventoryCheckOrder> getPage(int pageNum, int pageSize, String keyword) {
        return checkRepository.findPage(pageNum, pageSize, keyword);
    }

    public InventoryCheckOrder getById(Long id) {
        return checkRepository.findById(id);
    }

    @Transactional
    public InventoryCheckOrder create(InventoryCheckOrder order) {
        order.setId(null);
        order.setStatus("draft");
        order.setCheckDate(LocalDate.now());
        if (order.getCheckNo() == null || order.getCheckNo().isEmpty()) {
            order.setCheckNo(checkRepository.generateCheckNo());
        }
        InventoryCheckOrder saved = checkRepository.save(order);
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            checkRepository.saveItems(saved.getId(), order.getItems());
        }
        return saved;
    }

    @Transactional
    public InventoryCheckOrder update(Long id, InventoryCheckOrder order) {
        InventoryCheckOrder existing = checkRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("盘点单不存在");
        }
        if (!"draft".equals(existing.getStatus())) {
            throw new RuntimeException("只有草稿状态的盘点单可以编辑");
        }
        order.setId(id);
        order.setCheckNo(existing.getCheckNo());
        InventoryCheckOrder updated = checkRepository.save(order);
        if (order.getItems() != null) {
            checkRepository.saveItems(id, order.getItems());
        }
        return updated;
    }

    @Transactional
    public void submit(Long id) {
        InventoryCheckOrder order = checkRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("盘点单不存在");
        }
        if (!"draft".equals(order.getStatus())) {
            throw new RuntimeException("只有草稿状态的盘点单可以提交");
        }
        order.setStatus("submitted");
        checkRepository.save(order);
    }

    @Transactional
    public void confirm(Long id) {
        InventoryCheckOrder order = checkRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("盘点单不存在");
        }
        if (!"submitted".equals(order.getStatus())) {
            throw new RuntimeException("只有已提交的盘点单可以确认");
        }

        if (order.getItems() != null) {
            for (InventoryCheckItem item : order.getItems()) {
                Inventory inventory = inventoryRepository.findBySkuCodeAndWarehouse(
                        item.getSkuCode(), order.getWarehouseName());
                if (inventory != null) {
                    BigDecimal diff = item.getActualQty().subtract(item.getSystemQty());
                    inventory.setAvailableQty(inventory.getAvailableQty().add(diff));
                    inventory.setTotalQty(inventory.getTotalQty().add(diff));
                    inventoryRepository.save(inventory);
                }
            }
        }

        order.setStatus("completed");
        checkRepository.save(order);
    }
}
