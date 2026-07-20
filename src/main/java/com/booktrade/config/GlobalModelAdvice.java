package com.booktrade.config;

import com.booktrade.entity.User;
import com.booktrade.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final NotificationService notificationService;

    public GlobalModelAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadCount")
    public long addUnreadCount(HttpSession session) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            return 0;
        }
        return notificationService.countUnread(user.getId());
    }
}
