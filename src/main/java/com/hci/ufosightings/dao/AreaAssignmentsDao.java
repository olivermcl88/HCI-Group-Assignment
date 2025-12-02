package com.hci.ufosightings.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.AreaAssignmentsId;
import com.hci.ufosightings.common.AssignmentStatus;

@Repository
public interface AreaAssignmentsDao extends JpaRepository<AreaAssignments, AreaAssignmentsId> {

    List<AreaAssignments> findById_UserId(Long userId);

    List<AreaAssignments> findByStatus(AssignmentStatus status);

    @Query("SELECT COUNT(a) FROM AreaAssignments a WHERE a.id.userId = :userId")
    Long countByUserId(Long userId);
}
