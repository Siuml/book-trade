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
            model.addAttribute("error", "msg.login.error");
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
            model.addAttribute("error", "msg.register.password_mismatch");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "msg.register.password_too_short");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setPhone(phone);

        if (!userService.register(user)) {
            model.addAttribute("error", "msg.register.username_exists");
            return "register";
        }
        model.addAttribute("success", "msg.register.success");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@RequestParam String nickname,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String email,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "msg.profile.nickname.empty");
            return "redirect:/profile/edit";
        }

        boolean success = userService.updateProfile(user.getId(), nickname, phone, email);
        if (success) {
            user.setNickname(nickname);
            user.setPhone(phone);
            user.setEmail(email);
            session.setAttribute(LoginInterceptor.SESSION_USER, user);
            redirectAttributes.addFlashAttribute("success", "msg.profile.updated");
            return "redirect:/profile";
        } else {
            redirectAttributes.addFlashAttribute("error", "msg.profile.update_failed");
            return "redirect:/profile/edit";
        }
    }
}
