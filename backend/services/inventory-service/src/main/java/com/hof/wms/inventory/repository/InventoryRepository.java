package com.hof.wms.inventory.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.inventory.entity.Inventory;
import com.hof.wms.inventory.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryRepository {

    private final InventoryMapper inventoryMapper;

    public Page<Inventory> findPage(int pageNum, int pageSize, String keyword) {
        Page<Inventory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Inventory::getSkuCode, keyword)
                    .or().like(Inventory::getProductName, keyword)
                    .or().like(Inventory::getWarehouseName, keyword));
        }
        wrapper.orderByDesc(Inventory::getUpdatedAt);
        return inventoryMapper.selectPage(page, wrapper);
    }

    public Inventory findById(Long id) {
        return inventoryMapper.selectById(id);
    }

    public Inventory findBySkuCodeAndWarehouse(String skuCode, String warehouseName) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Inventory::getSkuCode, skuCode)
                .eq(Inventory::getWarehouseName, warehouseName);
        return inventoryMapper.selectOne(wrapper);
    }

    public List<Inventory> findByWarehouse(String warehouseName) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Inventory::getWarehouseName, warehouseName);
        return inventoryMapper.selectList(wrapper);
    }

    public List<Inventory> findAlertInventory() {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper();
        wrapper.apply("available_qty < safety_stock");
        wrapper.orderByAsc(Inventory::getAvailableQty);
        return inventoryMapper.selectList(wrapper);
    }

    public Inventory save(Inventory inventory) {
        inventory.setUpdatedAt(LocalDateTime.now());
        if (inventory.getId() == null) {
            inventoryMapper.insert(inventory);
        } else {
            inventoryMapper.updateById(inventory);
        }
        return inventory;
    }

    public void updateQuantity(Long id, BigDecimal availableQty, BigDecimal lockedQty, BigDecimal totalQty) {
        Inventory inventory = findById(id);
        if (inventory != null) {
            inventory.setAvailableQty(availableQty);
            inventory.setLockedQty(lockedQty);
            inventory.setTotalQty(totalQty);
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
        }
    }

    public void incrementQuantity(Long id, BigDecimal increment) {
        Inventory inventory = findById(id);
        if (inventory != null) {
            inventory.setAvailableQty(inventory.getAvailableQty().add(increment));
            inventory.setTotalQty(inventory.getTotalQty().add(increment));
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
        }
    }

    public void decrementQuantity(Long id, BigDecimal decrement) {
        Inventory inventory = findById(id);
        if (inventory != null) {
            BigDecimal newAvailable = inventory.getAvailableQty().subtract(decrement);
            inventory.setAvailableQty(newAvailable.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newAvailable);
            inventory.setTotalQty(inventory.getTotalQty().subtract(decrement));
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
        }
    }
}
