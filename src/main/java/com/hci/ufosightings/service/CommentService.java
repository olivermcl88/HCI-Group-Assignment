package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Comment;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.CommentDao;
import com.hci.ufosightings.dto.CommentWithUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    
    private final CommentDao commentDao;
    private final UserService userService;
    
    public List<CommentWithUser> getCommentsBySightingId(Long sightingId) {
        List<Comment> comments = commentDao.findBySightingIdOrderByCommentDateDesc(sightingId);
        
        return comments.stream()
                .map(this::convertToCommentWithUser)
                .collect(Collectors.toList());
    }
    
    private CommentWithUser convertToCommentWithUser(Comment comment) {
        CommentWithUser.CommentWithUserBuilder builder = CommentWithUser.builder()
                .commentId(comment.getCommentId())
                .sightingId(comment.getSightingId())
                .commentText(comment.getCommentText())
                .commentDate(comment.getCommentDate())
                .isAnonymous(comment.getIsAnonymous());
        
        if (!comment.getIsAnonymous() && comment.getCommenterUserId() != null) {
            User user = userService.getUserById(comment.getCommenterUserId());
            if (user != null) {
                builder.commenterUsername(user.getUsername())
                       .commenterFirstName(user.getFirstName());
            }
        }
        
        return builder.build();
    }
    
    public Comment addComment(Long sightingId, Long commenterUserId, String commentText, Boolean isAnonymous) {
        Comment comment = Comment.builder()
                .sightingId(sightingId)
                .commenterUserId(commenterUserId)
                .commentText(commentText)
                .commentDate(LocalDateTime.now())
                .isAnonymous(isAnonymous != null ? isAnonymous : false)
                .build();
        
        Comment savedComment = commentDao.save(comment);
        log.info("New comment added: ID={}, SightingID={}, UserID={}", 
                savedComment.getCommentId(), sightingId, commenterUserId);
        
        return savedComment;
    }
    
    public Optional<Comment> getCommentById(Long commentId) {
        return commentDao.findById(commentId);
    }
    
    public List<Comment> getCommentsByUser(Long userId) {
        return commentDao.findByCommenterUserId(userId);
    }
    
    public long getCommentCount(Long sightingId) {
        return commentDao.countBySightingId(sightingId);
    }
    
    public void deleteComment(Long commentId) {
        commentDao.deleteById(commentId);
        log.info("Comment deleted: ID={}", commentId);
    }
    
    public void deleteCommentsByPattern(String pattern) {
        List<Comment> allComments = commentDao.findAll();
        List<Comment> commentsToDelete = allComments.stream()
                .filter(comment -> comment.getCommentText().startsWith(pattern))
                .collect(Collectors.toList());
        
        for (Comment comment : commentsToDelete) {
            commentDao.deleteById(comment.getCommentId());
        }
        
        log.info("Deleted {} comments matching pattern: {}", commentsToDelete.size(), pattern);
    }
}