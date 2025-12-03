package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {
    
    List<Comment> findBySightingIdOrderByCommentDateDesc(Long sightingId);
    
    List<Comment> findByCommenterUserId(Long commenterUserId);
    
    long countBySightingId(Long sightingId);
}