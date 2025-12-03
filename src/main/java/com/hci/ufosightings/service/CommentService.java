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
                .isAnonymous(comment.getIsAnonymous())
                .attachmentFilename(comment.getAttachmentFilename())
                .attachmentOriginalName(comment.getAttachmentOriginalName())
                .parentCommentId(comment.getParentCommentId())
                .replyLevel(comment.getReplyLevel() != null ? comment.getReplyLevel() : 0);
        
        if (!comment.getIsAnonymous() && comment.getCommenterUserId() != null) {
            User user = userService.getUserById(comment.getCommenterUserId());
            if (user != null) {
                builder.commenterUsername(user.getUsername())
                       .commenterFirstName(user.getFirstName());
            }
        }
        
        // Get parent commenter info for context
        if (comment.getParentCommentId() != null) {
            Optional<Comment> parentComment = commentDao.findById(comment.getParentCommentId());
            if (parentComment.isPresent() && !parentComment.get().getIsAnonymous()) {
                User parentUser = userService.getUserById(parentComment.get().getCommenterUserId());
                if (parentUser != null) {
                    builder.parentCommenterUsername(parentUser.getUsername());
                }
            } else {
                builder.parentCommenterUsername("Anonymous");
            }
        }
        
        return builder.build();
    }
    
    public Comment addComment(Long sightingId, Long commenterUserId, String commentText, Boolean isAnonymous) {
        return addComment(sightingId, commenterUserId, commentText, isAnonymous, null, null, null);
    }
    
    public Comment addComment(Long sightingId, Long commenterUserId, String commentText, Boolean isAnonymous,
                            String attachmentFilename, String attachmentOriginalName) {
        return addComment(sightingId, commenterUserId, commentText, isAnonymous, attachmentFilename, attachmentOriginalName, null);
    }
    
    public Comment addComment(Long sightingId, Long commenterUserId, String commentText, Boolean isAnonymous,
                            String attachmentFilename, String attachmentOriginalName, Long parentCommentId) {
        Integer replyLevel = 0;
        
        // Calculate reply level based on parent comment
        if (parentCommentId != null) {
            Optional<Comment> parentComment = commentDao.findById(parentCommentId);
            if (parentComment.isPresent()) {
                replyLevel = (parentComment.get().getReplyLevel() != null ? parentComment.get().getReplyLevel() : 0) + 1;
                // Limit reply depth to 3 levels to prevent excessive nesting
                if (replyLevel > 3) {
                    replyLevel = 3;
                }
            }
        }
        
        Comment comment = Comment.builder()
                .sightingId(sightingId)
                .commenterUserId(commenterUserId)
                .commentText(commentText)
                .commentDate(LocalDateTime.now())
                .isAnonymous(isAnonymous != null ? isAnonymous : false)
                .attachmentFilename(attachmentFilename)
                .attachmentOriginalName(attachmentOriginalName)
                .parentCommentId(parentCommentId)
                .replyLevel(replyLevel)
                .build();
        
        Comment savedComment = commentDao.save(comment);
        log.info("New comment added: ID={}, SightingID={}, UserID={}, ParentID={}, Level={}", 
                savedComment.getCommentId(), sightingId, commenterUserId, parentCommentId, replyLevel);
        
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