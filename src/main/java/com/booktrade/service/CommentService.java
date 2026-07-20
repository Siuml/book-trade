package com.booktrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrade.entity.Book;
import com.booktrade.entity.Comment;
import com.booktrade.entity.Notification;
import com.booktrade.entity.User;
import com.booktrade.mapper.BookMapper;
import com.booktrade.mapper.CommentMapper;
import com.booktrade.mapper.NotificationMapper;
import com.booktrade.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final NotificationMapper notificationMapper;

    public CommentService(CommentMapper commentMapper, UserMapper userMapper,
                          BookMapper bookMapper, NotificationMapper notificationMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
        this.notificationMapper = notificationMapper;
    }

    public List<Comment> listByBookId(Long bookId) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getBookId, bookId)
                        .isNull(Comment::getParentId)
                        .orderByDesc(Comment::getCreateTime));

        for (Comment comment : comments) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUserNickname(user.getNickname());
            }
            List<Comment> replies = getRepliesByParentId(comment.getId());
            comment.setReplies(replies);
        }
        return comments;
    }

    public List<Comment> getRepliesByParentId(Long parentId) {
        List<Comment> replies = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, parentId)
                        .orderByAsc(Comment::getCreateTime));

        for (Comment reply : replies) {
            User user = userMapper.selectById(reply.getUserId());
            if (user != null) {
                reply.setUserNickname(user.getNickname());
            }
        }
        return replies;
    }

    public boolean create(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        boolean result = commentMapper.insert(comment) > 0;
        if (result) {
            Book book = bookMapper.selectById(comment.getBookId());
            if (book != null && !book.getSellerId().equals(comment.getUserId())) {
                User commenter = userMapper.selectById(comment.getUserId());
                String nickname = commenter != null ? commenter.getNickname() : "匿名用户";
                Notification notif = new Notification();
                notif.setUserId(book.getSellerId());
                notif.setType("comment");
                notif.setTitle("新留言");
                notif.setContent(nickname + " 在您的书籍《" + book.getTitle() + "》下发表了留言：" +
                        (comment.getContent().length() > 50 ? comment.getContent().substring(0, 50) + "..." : comment.getContent()));
                notif.setRelatedId(book.getId());
                notif.setIsRead(0);
                notif.setCreateTime(LocalDateTime.now());
                notificationMapper.insert(notif);
            }
        }
        return result;
    }

    public Comment getById(Long id) {
        return commentMapper.selectById(id);
    }

    public long countByBookId(Long bookId) {
        return commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getBookId, bookId));
    }
}
