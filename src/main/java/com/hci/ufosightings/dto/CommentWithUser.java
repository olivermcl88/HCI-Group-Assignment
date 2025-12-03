package com.hci.ufosightings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentWithUser {
    private Long commentId;
    private Long sightingId;
    private String commentText;
    private LocalDateTime commentDate;
    private Boolean isAnonymous;
    private String commenterUsername;
    private String commenterFirstName;
    private String attachmentFilename;
    private String attachmentOriginalName;
}