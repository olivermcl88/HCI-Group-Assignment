package com.hci.ufosightings.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hci.ufosightings.common.Area;
import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.AreaPriority;
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

    public Optional<Area> getAreaById(Long areaId) {
        return areaDao.findById(areaId);

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

    /**
     * Update area priority
     */
    public void updateAreaPriority(Long areaId, String priority) {
        Area area = areaDao.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found: " + areaId));

        AreaPriority areaPriority = AreaPriority.valueOf(priority.toUpperCase());
        area.setAreaPriority(areaPriority);
        areaDao.save(area);
    }

    /**
     * Get area details with all assignments
     */
    public Map<String, Object> getAreaDetailsWithAssignments(Long areaId) {
        Area area = areaDao.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found: " + areaId));

        // Get all assignments for this area
        List<AreaAssignments> assignments = areaAssignmentsDao.findAll().stream()
                .filter(a -> a.getId().getAreaId().equals(areaId))
                .collect(Collectors.toList());

        // Build response map
        Map<String, Object> response = new HashMap<>();
        response.put("areaId", area.getAreaId());
        response.put("areaName", area.getAreaName());
        response.put("description", area.getDescription());
        response.put("priority", area.getAreaPriority().toString());

        // Convert assignments to simple maps
        List<Map<String, Object>> assignedUsers = assignments.stream()
                .map(a -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", a.getUser().getUserId());
                    userMap.put("username", a.getUser().getUsername());
                    userMap.put("firstName", a.getUser().getFirstName());
                    userMap.put("secondName", a.getUser().getSecondName());
                    userMap.put("status", a.getStatus().toString());
                    userMap.put("assignedAt", a.getAssignedAt().toString());
                    return userMap;
                })
                .collect(Collectors.toList());

        response.put("assignedUsers", assignedUsers);

        return response;
    }
;
}
