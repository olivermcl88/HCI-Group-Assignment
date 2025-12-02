package com.hci.ufosightings.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.dao.AreaAssignmentsDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaAssignmentsService {

    private final AreaAssignmentsDao areaAssignmentsDao;

    public List<AreaAssignments> getAllAssignments() {
        return areaAssignmentsDao.findAll();
    }

    public List<AreaAssignments> getAssignmentsByUserId(Long userId) {
        return areaAssignmentsDao.findById_UserId(userId);
    }

    public Long getAssignmentCountByUserId(Long userId) {
        return areaAssignmentsDao.countByUserId(userId);
    }

    public Map<Long, Long> getAssignmentCountsForAllUsers() {
        List<AreaAssignments> allAssignments = areaAssignmentsDao.findAll();
        Map<Long, Long> counts = new HashMap<>();

        for (AreaAssignments assignment : allAssignments) {
            Long userId = assignment.getId().getUserId();
            counts.put(userId, counts.getOrDefault(userId, 0L) + 1);
        }

        return counts;
    }

    /**
     * Get all assignments grouped by user ID Returns a map where key is userId
     * and value is list of their assignments This includes full assignment
     * details: area info, status, assigned date, etc.
     */
    public Map<Long, List<AreaAssignments>> getAssignmentsGroupedByUser() {
        List<AreaAssignments> allAssignments = areaAssignmentsDao.findAll();
        Map<Long, List<AreaAssignments>> groupedAssignments = new HashMap<>();

        for (AreaAssignments assignment : allAssignments) {
            Long userId = assignment.getId().getUserId();
            groupedAssignments.computeIfAbsent(userId, k -> new ArrayList<>()).add(assignment);
        }

        return groupedAssignments;
    }
}
