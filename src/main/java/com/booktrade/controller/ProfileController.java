package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.Book;
import com.booktrade.entity.User;
import com.booktrade.service.BookService;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProfileController {

    private final UserService userService;
    private final BookService bookService;

    public ProfileController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @GetMapping("/seller/{id}")
    public String sellerProfile(@PathVariable Long id, HttpSession session, Model model) {
        User seller = userService.getById(id);
        if (seller == null) {
            return "redirect:/";
        }
        List<Book> books = bookService.listOnSaleBySeller(id);
        User loginUser = (User) session.getAttribute(LoginInterceptor.SESSION_USER);

        model.addAttribute("seller", seller);
        model.addAttribute("books", books);
        model.addAttribute("loginUser", loginUser);
        return "seller-profile";
    }

    @GetMapping("/profile")
    public String myProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        List<Book> books = bookService.listBySeller(user.getId());

        model.addAttribute("profileUser", user);
        model.addAttribute("books", books);
        model.addAttribute("loginUser", user);
        return "profile";
    }
}
