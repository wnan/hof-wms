package com.hof.wms.inbound.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.inbound.entity.InboundOrder;
import com.hof.wms.inbound.entity.InboundOrderItem;
import com.hof.wms.inbound.mapper.InboundOrderItemMapper;
import com.hof.wms.inbound.mapper.InboundOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InboundOrderRepository {

    private final InboundOrderMapper orderMapper;
    private final InboundOrderItemMapper itemMapper;

    public Page<InboundOrder> findPage(int pageNum, int pageSize, String keyword, String status) {
        Page<InboundOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(InboundOrder::getOrderNo, keyword)
                    .or().like(InboundOrder::getSupplierName, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(InboundOrder::getStatus, status);
        }
        wrapper.orderByDesc(InboundOrder::getCreatedAt);
        Page<InboundOrder> result = orderMapper.selectPage(page, wrapper);
        for (InboundOrder order : result.getRecords()) {
            order.setItems(findItemsByOrderId(order.getId()));
        }
        return result;
    }

    public InboundOrder findById(Long id) {
        InboundOrder order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(findItemsByOrderId(id));
        }
        return order;
    }

    public List<InboundOrderItem> findItemsByOrderId(Long orderId) {
        LambdaQueryWrapper<InboundOrderItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(InboundOrderItem::getOrderId, orderId);
        return itemMapper.selectList(wrapper);
    }

    public InboundOrder save(InboundOrder order) {
        if (order.getId() == null) {
            order.setCreatedAt(LocalDateTime.now());
            orderMapper.insert(order);
        } else {
            orderMapper.updateById(order);
        }
        return order;
    }

    public void saveItems(Long orderId, List<InboundOrderItem> items) {
        LambdaQueryWrapper<InboundOrderItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(InboundOrderItem::getOrderId, orderId);
        itemMapper.delete(wrapper);
        for (InboundOrderItem item : items) {
            item.setId(null);
            item.setOrderId(orderId);
            itemMapper.insert(item);
        }
    }

    public void deleteById(Long id) {
        LambdaQueryWrapper<InboundOrderItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(InboundOrderItem::getOrderId, id);
        itemMapper.delete(wrapper);
        orderMapper.deleteById(id);
    }

    public String generateOrderNo() {
        String date = java.time.LocalDate.now().toString().replace("-", "");
        LambdaQueryWrapper<InboundOrder> wrapper = new LambdaQueryWrapper();
        wrapper.likeRight(InboundOrder::getOrderNo, "RK" + date);
        long count = orderMapper.selectCount(wrapper) + 1;
        return "RK" + date + String.format("%03d", count);
    }
}
