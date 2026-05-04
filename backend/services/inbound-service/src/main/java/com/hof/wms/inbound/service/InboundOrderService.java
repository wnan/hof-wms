package com.hof.wms.inbound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.inbound.entity.InboundOrder;
import com.hof.wms.inbound.entity.InboundOrderItem;
import com.hof.wms.inbound.repository.InboundOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboundOrderService {

    private final InboundOrderRepository orderRepository;

    public Page<InboundOrder> getPage(int pageNum, int pageSize, String keyword, String status) {
        return orderRepository.findPage(pageNum, pageSize, keyword, status);
    }

    public InboundOrder getById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public InboundOrder create(InboundOrder order) {
        order.setId(null);
        order.setStatus("draft");
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo(orderRepository.generateOrderNo());
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(calculateTotalAmount(order.getItems()));
        }
        InboundOrder saved = orderRepository.save(order);
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            orderRepository.saveItems(saved.getId(), order.getItems());
        }
        return saved;
    }

    @Transactional
    public InboundOrder update(Long id, InboundOrder order) {
        InboundOrder existing = orderRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("入库单不存在");
        }
        if (!"draft".equals(existing.getStatus())) {
            throw new RuntimeException("只有草稿状态的单据可以编辑");
        }
        order.setId(id);
        order.setOrderNo(existing.getOrderNo());
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(calculateTotalAmount(order.getItems()));
        }
        InboundOrder updated = orderRepository.save(order);
        if (order.getItems() != null) {
            orderRepository.saveItems(id, order.getItems());
        }
        return updated;
    }

    @Transactional
    public void submit(Long id) {
        InboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("入库单不存在");
        }
        if (!"draft".equals(order.getStatus())) {
            throw new RuntimeException("只有草稿状态的单据可以提交");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("入库单明细不能为空");
        }
        order.setStatus("submitted");
        orderRepository.save(order);
    }

    @Transactional
    public void approve(Long id) {
        InboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("入库单不存在");
        }
        if (!"submitted".equals(order.getStatus())) {
            throw new RuntimeException("只有已提交的单据可以审核");
        }
        order.setStatus("approved");
        orderRepository.save(order);
    }

    @Transactional
    public void confirm(Long id) {
        InboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("入库单不存在");
        }
        if (!"approved".equals(order.getStatus())) {
            throw new RuntimeException("只有已审核的单据可以确认入库");
        }
        order.setStatus("completed");
        orderRepository.save(order);
    }

    public void delete(Long id) {
        InboundOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new RuntimeException("入库单不存在");
        }
        if (!"draft".equals(order.getStatus())) {
            throw new RuntimeException("只有草稿状态的单据可以删除");
        }
        orderRepository.deleteById(id);
    }

    private BigDecimal calculateTotalAmount(List<InboundOrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(InboundOrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
