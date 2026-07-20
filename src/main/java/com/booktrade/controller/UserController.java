package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.User;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
        session.setAttribute(LoginInterceptor.SESSION_USER, user);
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String nickname,
                           @RequestParam(required = false) String phone,
                           Model model) {
        if (password == null || !password.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "密码长度至少6位");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setPhone(phone);

        if (!userService.register(user)) {
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
        model.addAttribute("success", "注册成功，请登录");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
