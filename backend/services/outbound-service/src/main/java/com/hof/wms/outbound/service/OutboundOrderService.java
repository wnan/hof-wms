package com.hof.wms.outbound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.outbound.entity.OutboundOrder;
import com.hof.wms.outbound.entity.OutboundOrderItem;
import com.hof.wms.outbound.repository.OutboundOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboundOrderService {

    private final OutboundOrderRepository orderRepository;

    public Page<OutboundOrder> getPage(int pageNum, int pageSize, String keyword, String status) {
        return orderRepository.findPage(pageNum, pageSize, keyword, status);
    }

    public OutboundOrder getById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public OutboundOrder create(OutboundOrder order) {
        order.setId(null);
        order.setStatus("draft");
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo(orderRepository.generateOrderNo());
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(calculateTotalAmount(order.getItems()));
        }
        OutboundOrder saved = orderRepository.save(order);
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            orderRepository.saveItems(saved.getId(), order.getItems());
        }
        return saved;
    }

    @Transactional
    public OutboundOrder update(Long id, OutboundOrder order) {
        OutboundOrder existing = orderRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("出库单不存在");
        }
        if (!"draft".equals(existing.getStatus())) {
            throw new RuntimeException("只有草稿状态的单据可以编辑");
        }
        order.setId(id);
        order.setOrderNo(existing.getOrderNo());
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(calculateTotalAmount(order.getItems()));
        }
        OutboundOrder updated = orderRepository.save(order);
        if (order.getItems() != null) {
            orderRepository.saveItems(id, order.getItems());
        }
        return updated;
    }

    @Transactional
    public void submit(Long id) {
        OutboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("出库单不存在");
        }
        if (!"draft".equals(order.getStatus())) {
            throw new RuntimeException("只有草稿状态的单据可以提交");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("出库单明细不能为空");
        }
        order.setStatus("submitted");
        orderRepository.save(order);
    }

    @Transactional
    public void approve(Long id) {
        OutboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("出库单不存在");
        }
        if (!"submitted".equals(order.getStatus())) {
            throw new RuntimeException("只有已提交的单据可以审核");
        }
        order.setStatus("approved");
        orderRepository.save(order);
    }

    @Transactional
    public void confirm(Long id) {
        OutboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("出库单不存在");
        }
        if (!"approved".equals(order.getStatus())) {
            throw new RuntimeException("只有已审核的单据可以确认出库");
        }
        order.setStatus("completed");
        orderRepository.save(order);
    }

    public void delete(Long id) {
        OutboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("出库单不存在");
        }
        if (!"draft".equals(order.getStatus())) {
            throw new RuntimeException("只有草稿状态的单据可以删除");
        }
        orderRepository.deleteById(id);
    }

    private BigDecimal calculateTotalAmount(List<OutboundOrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(OutboundOrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
