package com.hof.wms.inventory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.inventory.entity.Inventory;
import com.hof.wms.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Page<Inventory> getPage(int pageNum, int pageSize, String keyword) {
        return inventoryRepository.findPage(pageNum, pageSize, keyword);
    }

    public Inventory getById(Long id) {
        return inventoryRepository.findById(id);
    }

    public List<Inventory> getAlertList() {
        return inventoryRepository.findAlertInventory();
    }

    public Map<String, Object> getDashboardSummary() {
        List<Inventory> alertList = inventoryRepository.findAlertInventory();
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalInventoryCount", inventoryRepository.findPage(1, 1, null).getTotal());
        summary.put("alertCount", alertList.size());
        summary.put("alertItems", alertList);
        return summary;
    }

    @Transactional
    public void adjustSafetyStock(Long id, BigDecimal newSafetyStock) {
        Inventory inventory = inventoryRepository.findById(id);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        inventory.setSafetyStock(newSafetyStock);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void adjustQuantity(Long id, BigDecimal adjustment) {
        Inventory inventory = inventoryRepository.findById(id);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }
        inventory.setAvailableQty(inventory.getAvailableQty().add(adjustment));
        inventory.setTotalQty(inventory.getTotalQty().add(adjustment));
        inventoryRepository.save(inventory);
    }
}
