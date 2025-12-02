package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.AreaAssignmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaAssignmentsDao extends JpaRepository<AreaAssignments, AreaAssignmentsId> {

    List<AreaAssignments> findById_UserId(Long userId);
    
    @Query("SELECT COUNT(a) FROM AreaAssignments a WHERE a.id.userId = :userId")
    Long countByUserId(Long userId);
}

