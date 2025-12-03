package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Sighting;
import com.hci.ufosightings.dao.SightingDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SightingService {
    
    private final SightingDao sightingDao;
    
    public List<Sighting> getAllSightings() {
        return sightingDao.findAll();
    }
    
    public Optional<Sighting> getSightingById(Long id) {
        return sightingDao.findById(id);
    }
    
    public List<Sighting> getSightingsByReporter(Long reporterUserId) {
        return sightingDao.findByReporterUserId(reporterUserId);
    }
    
    public List<Sighting> getSightingsByLocation(String location) {
        return sightingDao.findByLocationContainingIgnoreCase(location);
    }
    
    public List<Sighting> getSightingsByShape(String shape) {
        return sightingDao.findByShape(shape);
    }
    
    public Sighting saveSighting(Sighting sighting) {
        return sightingDao.save(sighting);
    }
    
    public void deleteSighting(Long id) {
        sightingDao.deleteById(id);
    }
    
    // Method to vote on a sighting
    public Sighting voteOnSighting(Long sightingId, String voteType) {
        Optional<Sighting> optionalSighting = sightingDao.findById(sightingId);
        if (optionalSighting.isPresent()) {
            Sighting sighting = optionalSighting.get();
            switch (voteType.toLowerCase()) {
                case "legit":
                    sighting.setLegitVotes((sighting.getLegitVotes() != null ? sighting.getLegitVotes() : 0) + 1);
                    break;
                case "uncertain":
                    sighting.setUncertainVotes((sighting.getUncertainVotes() != null ? sighting.getUncertainVotes() : 0) + 1);
                    break;
                case "hoax":
                    sighting.setHoaxVotes((sighting.getHoaxVotes() != null ? sighting.getHoaxVotes() : 0) + 1);
                    break;
            }
            return sightingDao.save(sighting);
        }
        return null;
    }
}