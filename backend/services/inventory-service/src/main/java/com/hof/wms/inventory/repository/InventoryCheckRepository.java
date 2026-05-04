package com.hof.wms.inventory.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.inventory.entity.InventoryCheckItem;
import com.hof.wms.inventory.entity.InventoryCheckOrder;
import com.hof.wms.inventory.mapper.InventoryCheckItemMapper;
import com.hof.wms.inventory.mapper.InventoryCheckOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryCheckRepository {

    private final InventoryCheckOrderMapper orderMapper;
    private final InventoryCheckItemMapper itemMapper;

    public Page<InventoryCheckOrder> findPage(int pageNum, int pageSize, String keyword) {
        Page<InventoryCheckOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InventoryCheckOrder> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(InventoryCheckOrder::getCheckNo, keyword)
                    .or().like(InventoryCheckOrder::getWarehouseName, keyword));
        }
        wrapper.orderByDesc(InventoryCheckOrder::getCreatedAt);
        Page<InventoryCheckOrder> result = orderMapper.selectPage(page, wrapper);
        for (InventoryCheckOrder order : result.getRecords()) {
            order.setItems(findItemsByOrderId(order.getId()));
        }
        return result;
    }

    public InventoryCheckOrder findById(Long id) {
        InventoryCheckOrder order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(findItemsByOrderId(id));
        }
        return order;
    }

    public List<InventoryCheckItem> findItemsByOrderId(Long orderId) {
        LambdaQueryWrapper<InventoryCheckItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(InventoryCheckItem::getCheckOrderId, orderId);
        return itemMapper.selectList(wrapper);
    }

    public InventoryCheckOrder save(InventoryCheckOrder order) {
        if (order.getId() == null) {
            order.setCreatedAt(LocalDateTime.now());
            orderMapper.insert(order);
        } else {
            orderMapper.updateById(order);
        }
        return order;
    }

    public void saveItems(Long orderId, List<InventoryCheckItem> items) {
        LambdaQueryWrapper<InventoryCheckItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(InventoryCheckItem::getCheckOrderId, orderId);
        itemMapper.delete(wrapper);
        for (InventoryCheckItem item : items) {
            item.setId(null);
            item.setCheckOrderId(orderId);
            itemMapper.insert(item);
        }
    }

    public String generateCheckNo() {
        String date = LocalDate.now().toString().replace("-", "");
        LambdaQueryWrapper<InventoryCheckOrder> wrapper = new LambdaQueryWrapper();
        wrapper.likeRight(InventoryCheckOrder::getCheckNo, "PD" + date);
        long count = orderMapper.selectCount(wrapper) + 1;
        return "PD" + date + String.format("%03d", count);
    }
}
