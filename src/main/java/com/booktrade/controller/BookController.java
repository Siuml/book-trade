package com.booktrade.controller;

import com.booktrade.config.LoginInterceptor;
import com.booktrade.entity.Book;
import com.booktrade.entity.Category;
import com.booktrade.entity.Comment;
import com.booktrade.entity.User;
import com.booktrade.service.BookService;
import com.booktrade.service.CategoryService;
import com.booktrade.service.CommentService;
import com.booktrade.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final CommentService commentService;

    @Value("${book-trade.upload-path}")
    private String uploadPath;

    public BookController(BookService bookService, CategoryService categoryService, UserService userService, CommentService commentService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Book book = bookService.getById(id);
        if (book == null) {
            return "redirect:/";
        }
        Category category = categoryService.getById(book.getCategoryId());
        User seller = userService.getById(book.getSellerId());
        User loginUser = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        List<Comment> comments = commentService.listByBookId(id);

        model.addAttribute("book", book);
        model.addAttribute("category", category);
        model.addAttribute("seller", seller);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("comments", comments);
        return "book-detail";
    }

    @PostMapping("/comment/{bookId}")
    public String addComment(@PathVariable Long bookId,
                             @RequestParam String content,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "msg.need_login");
            return "redirect:/login";
        }

        Comment comment = new Comment();
        comment.setBookId(bookId);
        comment.setUserId(user.getId());
        comment.setContent(content);

        boolean success = commentService.create(comment);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "msg.comment.added");
        } else {
            redirectAttributes.addFlashAttribute("error", "msg.comment.add_failed");
        }
        return "redirect:/book/detail/" + bookId;
    }

    @PostMapping("/reply/{parentId}")
    public String addReply(@PathVariable Long parentId,
                           @RequestParam String content,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "msg.need_login");
            return "redirect:/login";
        }

        Comment parentComment = commentService.getById(parentId);
        if (parentComment == null) {
            redirectAttributes.addFlashAttribute("error", "msg.comment.not_found");
            return "redirect:/";
        }

        if (content == null || content.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "msg.reply.empty_content");
            return "redirect:/book/detail/" + parentComment.getBookId();
        }

        Comment reply = new Comment();
        reply.setBookId(parentComment.getBookId());
        reply.setUserId(user.getId());
        reply.setParentId(parentId);
        reply.setContent(content);

        boolean success = commentService.create(reply);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "msg.reply.added");
        } else {
            redirectAttributes.addFlashAttribute("error", "msg.reply.add_failed");
        }
        return "redirect:/book/detail/" + parentComment.getBookId();
    }

    @GetMapping("/publish")
    public String publishPage(Model model) {
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping("/publish")
    public String publish(@RequestParam String title,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String isbn,
                          @RequestParam BigDecimal price,
                          @RequestParam(required = false) String description,
                          @RequestParam Long categoryId,
                          @RequestParam(defaultValue = "0") Integer condition,
                          @RequestParam(required = false) MultipartFile coverImage,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) throws IOException {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setDescription(description);
        book.setCategoryId(categoryId);
        book.setCondition(condition);
        book.setSellerId(user.getId());

        if (coverImage != null && !coverImage.isEmpty()) {
            book.setCoverImage(saveImage(coverImage));
        }

        bookService.save(book);
        redirectAttributes.addFlashAttribute("success", "msg.book.published");
        return "redirect:/book/my";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book book = bookService.getById(id);
        if (book == null || !book.getSellerId().equals(user.getId())) {
            return "redirect:/book/my";
        }
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.listAll());
        return "book-form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam(required = false) String author,
                       @RequestParam(required = false) String isbn,
                       @RequestParam BigDecimal price,
                       @RequestParam(required = false) String description,
                       @RequestParam Long categoryId,
                       @RequestParam(defaultValue = "0") Integer condition,
                       @RequestParam(required = false) MultipartFile coverImage,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) throws IOException {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book existing = bookService.getById(id);
        if (existing == null || !existing.getSellerId().equals(user.getId())) {
            return "redirect:/book/my";
        }

        existing.setTitle(title);
        existing.setAuthor(author);
        existing.setIsbn(isbn);
        existing.setPrice(price);
        existing.setDescription(description);
        existing.setCategoryId(categoryId);
        existing.setCondition(condition);

        if (coverImage != null && !coverImage.isEmpty()) {
            existing.setCoverImage(saveImage(coverImage));
        }

        bookService.update(existing);
        redirectAttributes.addFlashAttribute("success", "msg.book.updated");
        return "redirect:/book/my";
    }

    @GetMapping("/my")
    public String myBooks(HttpSession session, Model model) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        List<Book> books = bookService.listBySeller(user.getId());
        model.addAttribute("books", books);
        model.addAttribute("loginUser", user);
        return "my-books";
    }

    @GetMapping("/off/{id}")
    public String offShelf(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute(LoginInterceptor.SESSION_USER);
        Book book = bookService.getById(id);
        if (book != null && book.getSellerId().equals(user.getId())) {
            bookService.offShelf(id);
            redirectAttributes.addFlashAttribute("success", "msg.book.off_shelf");
        }
        return "redirect:/book/my";
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";
        String fileName = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(fileName));
        return "/uploads/" + fileName;
    }
}
