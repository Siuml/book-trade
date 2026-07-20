package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Book;
import com.booktrade.mapper.BookMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BookService {

    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public List<Book> listOnSale(String keyword, Long categoryId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, 1)
                .orderByDesc(Book::getCreateTime);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getDescription, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        return bookMapper.selectList(wrapper);
    }

    public List<Book> listBySeller(Long sellerId) {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getSellerId, sellerId)
                        .orderByDesc(Book::getCreateTime));
    }

    public List<Book> listAll() {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>().orderByDesc(Book::getCreateTime));
    }

    public Book getById(Long id) {
        return bookMapper.selectById(id);
    }

    public boolean save(Book book) {
        book.setStatus(1);
        return bookMapper.insert(book) > 0;
    }

    public boolean update(Book book) {
        return bookMapper.updateById(book) > 0;
    }

    public boolean offShelf(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setStatus(0);
        return bookMapper.updateById(book) > 0;
    }

    public boolean markSold(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setStatus(2);
        return bookMapper.updateById(book) > 0;
    }

    public List<Book> listOnSaleBySeller(Long sellerId) {
        return bookMapper.selectList(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getSellerId, sellerId)
                        .eq(Book::getStatus, 1)
                        .orderByDesc(Book::getCreateTime));
    }

    public boolean delete(Long id) {
        return bookMapper.deleteById(id) > 0;
    }
}

