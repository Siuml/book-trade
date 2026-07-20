package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Notification;
import com.booktrade.mapper.NotificationMapper;
import com.booktrade.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    public NotificationService(NotificationMapper notificationMapper, UserMapper userMapper) {
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
    }

    public List<Notification> listByUser(Long userId) {
        return notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime));
    }

    public long countUnread(Long userId) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0));
    }

    public boolean markAsRead(Long id, Long userId) {
        Notification notification = notificationMapper.selectById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            return notificationMapper.updateById(notification) > 0;
        }
        return false;
    }

    public boolean markAllAsRead(Long userId) {
        List<Notification> unreadList = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0));
        for (Notification n : unreadList) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
        return true;
    }

    public boolean create(Notification notification) {
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        return notificationMapper.insert(notification) > 0;
    }

    public boolean broadcastToAll(String title, String content, String type) {
        List<Long> userIds = userMapper.selectList(null).stream()
                .map(u -> u.getId())
                .toList();
        for (Long uid : userIds) {
            Notification n = new Notification();
            n.setUserId(uid);
            n.setType(type);
            n.setTitle(title);
            n.setContent(content);
            n.setIsRead(0);
            n.setCreateTime(LocalDateTime.now());
            notificationMapper.insert(n);
        }
        return true;
    }
}
