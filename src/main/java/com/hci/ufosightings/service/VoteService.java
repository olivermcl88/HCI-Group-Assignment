package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Vote;
import com.hci.ufosightings.dao.VoteDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {
    
    private final VoteDao voteDao;
    
    @Transactional
    public Vote castVote(Long sightingId, Long userId, String voteType) {
        Vote.VoteType type;
        try {
            type = Vote.VoteType.valueOf(voteType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vote type: " + voteType);
        }
        
        // Check if user has already voted on this sighting
        Optional<Vote> existingVote = voteDao.findBySightingIdAndUserId(sightingId, userId);
        
        Vote vote;
        if (existingVote.isPresent()) {
            // Update existing vote
            vote = existingVote.get();
            log.info("Updating vote for user {} on sighting {} from {} to {}", 
                userId, sightingId, vote.getVoteType(), type);
            vote.setVoteType(type);
            vote.setVoteDate(LocalDateTime.now());
        } else {
            // Create new vote
            vote = Vote.builder()
                .sightingId(sightingId)
                .userId(userId)
                .voteType(type)
                .voteDate(LocalDateTime.now())
                .build();
            log.info("Creating new vote for user {} on sighting {} with type {}", 
                userId, sightingId, type);
        }
        
        return voteDao.save(vote);
    }
    
    public Optional<Vote> getUserVote(Long sightingId, Long userId) {
        return voteDao.findBySightingIdAndUserId(sightingId, userId);
    }
    
    public long getLegitVotes(Long sightingId) {
        return voteDao.countBySightingIdAndVoteType(sightingId, Vote.VoteType.LEGIT);
    }
    
    public long getUncertainVotes(Long sightingId) {
        return voteDao.countBySightingIdAndVoteType(sightingId, Vote.VoteType.UNCERTAIN);
    }
    
    public long getHoaxVotes(Long sightingId) {
        return voteDao.countBySightingIdAndVoteType(sightingId, Vote.VoteType.HOAX);
    }
    
    public long getTotalVotes(Long sightingId) {
        return voteDao.findBySightingId(sightingId).size();
    }
}