package com.hci.ufosightings.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hci.ufosightings.common.Area;
import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.AreaAssignmentsDao;
import com.hci.ufosightings.dao.AreaDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaService {

    private final AreaDao areaDao;
    private final AreaAssignmentsDao areaAssignmentsDao;

    public List<Area> getAllAreas() {
        return areaDao.findAll();
    }

    public List<User> getAssignedUsers(Long areaId) {
        return areaDao.findById(areaId)
                .map(Area::getAssignedUsers)
                .orElse(Collections.emptyList());
    }

    /**
     * Get areas that have no assignments (unassigned areas)
     */
    public List<Area> getUnassignedAreas() {
        List<Area> allAreas = areaDao.findAll();
        List<AreaAssignments> allAssignments = areaAssignmentsDao.findAll();

        // Get list of area IDs that have assignments
        List<Long> assignedAreaIds = allAssignments.stream()
                .map(a -> a.getId().getAreaId())
                .distinct()
                .collect(Collectors.toList());

        // Filter to only areas with no assignments
        return allAreas.stream()
                .filter(area -> !assignedAreaIds.contains(area.getAreaId()))
                .collect(Collectors.toList());
    }
}
