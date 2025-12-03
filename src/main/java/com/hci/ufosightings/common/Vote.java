package com.hci.ufosightings.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;
    
    @Column(name = "sighting_id", nullable = false)
    private Long sightingId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "vote_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteType voteType;
    
    @Column(name = "vote_date", nullable = false)
    private LocalDateTime voteDate;
    
    // Ensure one vote per user per sighting
    @Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sighting_id", "user_id"})
    })
    
    public enum VoteType {
        LEGIT, UNCERTAIN, HOAX
    }
}