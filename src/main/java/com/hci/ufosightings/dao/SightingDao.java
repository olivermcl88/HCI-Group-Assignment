package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.Sighting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SightingDao extends JpaRepository<Sighting, Long> {
    
    List<Sighting> findByReporterUserId(Long reporterUserId);
    
    List<Sighting> findByLocationContainingIgnoreCase(String location);
    
    List<Sighting> findByShape(String shape);
}