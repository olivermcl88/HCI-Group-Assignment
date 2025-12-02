package com.hci.ufosightings.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "sightings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sighting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sighting_id")
    private Long sightingId;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "reporter_user_id")
    private Long reporterUserId;
    
    @Column(name = "sighting_date")
    private LocalDate sightingDate;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "shape")
    private String shape;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "legit_votes")
    private Integer legitVotes;
    
    @Column(name = "uncertain_votes")
    private Integer uncertainVotes;
    
    @Column(name = "hoax_votes")
    private Integer hoaxVotes;
    
    // Helper methods for validation percentages
    public int getTotalVotes() {
        return (legitVotes != null ? legitVotes : 0) + 
               (uncertainVotes != null ? uncertainVotes : 0) + 
               (hoaxVotes != null ? hoaxVotes : 0);
    }
    
    public int getLegitPercentage() {
        int total = getTotalVotes();
        return total > 0 ? Math.round((float) legitVotes / total * 100) : 0;
    }
    
    public int getUncertainPercentage() {
        int total = getTotalVotes();
        return total > 0 ? Math.round((float) uncertainVotes / total * 100) : 0;
    }
    
    public int getHoaxPercentage() {
        int total = getTotalVotes();
        return total > 0 ? Math.round((float) hoaxVotes / total * 100) : 0;
    }
}