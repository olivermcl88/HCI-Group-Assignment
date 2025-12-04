package com.hci.ufosightings.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;
    
    @Column(name = "sighting_id")
    private Long sightingId;
    
    @Column(name = "commenter_user_id")
    private Long commenterUserId;
    
    @Column(name = "comment_text", columnDefinition = "TEXT")
    private String commentText;
    
    @Column(name = "comment_date")
    private LocalDateTime commentDate;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;
    
    @Column(name = "attachment_filename")
    private String attachmentFilename;
    
    @Column(name = "attachment_original_name")
    private String attachmentOriginalName;
    
    @Column(name = "parent_comment_id")
    private Long parentCommentId;
    
    @Column(name = "reply_level")
    private Integer replyLevel;
}