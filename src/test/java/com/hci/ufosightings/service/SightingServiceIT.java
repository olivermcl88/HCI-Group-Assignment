package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Sighting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SightingServiceIT {

    @Autowired
    private SightingService sightingService;

    @Test
    void saveSighting_assignsId_andPersists() {
        Sighting s = Sighting.builder()
                .title("Integration test sighting")
                .reporterUserId(1L)
                .sightingDate(LocalDate.now())
                .durationMinutes(5)
                .location("Test location")
                .latitude(51.5)
                .longitude(-0.12)
                .shape("Unknown")
                .description("Desc")
                .legitVotes(0)
                .uncertainVotes(0)
                .hoaxVotes(0)
                .build();

        Sighting saved = sightingService.saveSighting(s);
        assertNotNull(saved);
        assertNotNull(saved.getSightingId(), "Saved sighting should have an ID assigned by the DB");
    }
}

