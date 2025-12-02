package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.AreaAssignmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaAssignmentsDao extends JpaRepository<AreaAssignments, AreaAssignmentsId> {

    List<AreaAssignments> findByUser_UserId(Long userId);

}

