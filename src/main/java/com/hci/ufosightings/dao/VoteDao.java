package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteDao extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findBySightingIdAndUserId(Long sightingId, Long userId);
    
    List<Vote> findBySightingId(Long sightingId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.sightingId = :sightingId AND v.voteType = :voteType")
    long countBySightingIdAndVoteType(@Param("sightingId") Long sightingId, @Param("voteType") Vote.VoteType voteType);
}