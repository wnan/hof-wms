package com.hof.wms.outbound.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.outbound.entity.OutboundOrder;
import com.hof.wms.outbound.entity.OutboundOrderItem;
import com.hof.wms.outbound.mapper.OutboundOrderItemMapper;
import com.hof.wms.outbound.mapper.OutboundOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboundOrderRepository {

    private final OutboundOrderMapper orderMapper;
    private final OutboundOrderItemMapper itemMapper;

    public Page<OutboundOrder> findPage(int pageNum, int pageSize, String keyword, String status) {
        Page<OutboundOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(OutboundOrder::getOrderNo, keyword)
                    .or().like(OutboundOrder::getCustomerName, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(OutboundOrder::getStatus, status);
        }
        wrapper.orderByDesc(OutboundOrder::getCreatedAt);
        Page<OutboundOrder> result = orderMapper.selectPage(page, wrapper);
        for (OutboundOrder order : result.getRecords()) {
            order.setItems(findItemsByOrderId(order.getId()));
        }
        return result;
    }

    public OutboundOrder findById(Long id) {
        OutboundOrder order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(findItemsByOrderId(id));
        }
        return order;
    }

    public List<OutboundOrderItem> findItemsByOrderId(Long orderId) {
        LambdaQueryWrapper<OutboundOrderItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(OutboundOrderItem::getOrderId, orderId);
        return itemMapper.selectList(wrapper);
    }

    public OutboundOrder save(OutboundOrder order) {
        if (order.getId() == null) {
            order.setCreatedAt(LocalDateTime.now());
            orderMapper.insert(order);
        } else {
            orderMapper.updateById(order);
        }
        return order;
    }

    public void saveItems(Long orderId, List<OutboundOrderItem> items) {
        LambdaQueryWrapper<OutboundOrderItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(OutboundOrderItem::getOrderId, orderId);
        itemMapper.delete(wrapper);
        for (OutboundOrderItem item : items) {
            item.setId(null);
            item.setOrderId(orderId);
            itemMapper.insert(item);
        }
    }

    public void deleteById(Long id) {
        LambdaQueryWrapper<OutboundOrderItem> wrapper = new LambdaQueryWrapper();
        wrapper.eq(OutboundOrderItem::getOrderId, id);
        itemMapper.delete(wrapper);
        orderMapper.deleteById(id);
    }

    public String generateOrderNo() {
        String date = java.time.LocalDate.now().toString().replace("-", "");
        LambdaQueryWrapper<OutboundOrder> wrapper = new LambdaQueryWrapper();
        wrapper.likeRight(OutboundOrder::getOrderNo, "CK" + date);
        long count = orderMapper.selectCount(wrapper) + 1;
        return "CK" + date + String.format("%03d", count);
    }
}
