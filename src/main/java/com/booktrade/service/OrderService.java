package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Book;
import com.booktrade.entity.Notification;
import com.booktrade.entity.OrderLog;
import com.booktrade.entity.TradeOrder;
import com.booktrade.mapper.BookMapper;
import com.booktrade.mapper.NotificationMapper;
import com.booktrade.mapper.OrderLogMapper;
import com.booktrade.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderLogMapper orderLogMapper;
    private final BookMapper bookMapper;
    private final NotificationMapper notificationMapper;

    public OrderService(OrderMapper orderMapper, OrderLogMapper orderLogMapper,
                        BookMapper bookMapper, NotificationMapper notificationMapper) {
        this.orderMapper = orderMapper;
        this.orderLogMapper = orderLogMapper;
        this.bookMapper = bookMapper;
        this.notificationMapper = notificationMapper;
    }

    public List<TradeOrder> listByBuyer(Long buyerId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<TradeOrder>()
                        .eq(TradeOrder::getBuyerId, buyerId)
                        .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listBySeller(Long sellerId) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<TradeOrder>()
                        .eq(TradeOrder::getSellerId, sellerId)
                        .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listAll() {
        return orderMapper.selectList(
                new LambdaQueryWrapper<TradeOrder>().orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listByStatus(Integer status) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<TradeOrder>()
                        .eq(TradeOrder::getStatus, status)
                        .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return orderMapper.selectList(
                new LambdaQueryWrapper<TradeOrder>()
                        .ge(TradeOrder::getCreateTime, startTime)
                        .le(TradeOrder::getCreateTime, endTime)
                        .orderByDesc(TradeOrder::getCreateTime));
    }

    public List<TradeOrder> listByStatusAndTimeRange(Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TradeOrder::getStatus, status);
        }
        if (startTime != null) {
            wrapper.ge(TradeOrder::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(TradeOrder::getCreateTime, endTime);
        }
        wrapper.orderByDesc(TradeOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    public TradeOrder getById(Long id) {
        return orderMapper.selectById(id);
    }

    public boolean create(TradeOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);
        boolean result = orderMapper.insert(order) > 0;
        if (result) {
            addLog(order.getId(), null, null, "订单创建");
            Book book = bookMapper.selectById(order.getBookId());
            if (book != null) {
                Notification notif = new Notification();
                notif.setUserId(order.getSellerId());
                notif.setType("order");
                notif.setTitle("书籍被下单");
                notif.setContent("您的书籍《" + book.getTitle() + "》已被下单，订单编号：" + order.getOrderNo() + "，请及时确认。");
                notif.setRelatedId(order.getId());
                notif.setIsRead(0);
                notif.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(notif);
            }
        }
        return result;
    }

    public boolean updateStatus(Long id, Integer status, Long operatorId, String operatorName) {
        TradeOrder order = new TradeOrder();
        order.setId(id);
        order.setStatus(status);
        boolean result = orderMapper.updateById(order) > 0;
        if (result && operatorId != null) {
            String action = "";
            switch (status) {
                case 0: action = "订单重置为待确认"; break;
                case 1: action = "管理员确认订单"; break;
                case 2: action = "订单完成"; break;
                case 3: action = "订单取消"; break;
            }
            addLog(id, operatorId, operatorName, action);
        }
        return result;
    }

    public boolean updateStatus(Long id, Integer status) {
        return updateStatus(id, status, null, null);
    }

    public List<OrderLog> getLogsByOrderId(Long orderId) {
        return orderLogMapper.selectList(
                new LambdaQueryWrapper<OrderLog>()
                        .eq(OrderLog::getOrderId, orderId)
                        .orderByDesc(OrderLog::getCreateTime));
    }

    private void addLog(Long orderId, Long operatorId, String operatorName, String action) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setCreateTime(LocalDateTime.now());
        orderLogMapper.insert(log);
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + time + random;
    }
}
