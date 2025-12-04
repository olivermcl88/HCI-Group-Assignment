package com.hci.ufosightings.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hci.ufosightings.common.Area;
import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.AreaAssignmentsDao;
import com.hci.ufosightings.dao.AreaDao;
import com.hci.ufosightings.dao.UserDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaAssignmentsService {

    private final AreaAssignmentsDao areaAssignmentsDao;
    private final UserDao userDao;
    private final AreaDao areaDao;

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
        // Get all users to ensure we return counts for everyone (even if 0)
        List<User> allUsers = userDao.findAll();
        Map<Long, Long> counts = allUsers.stream()
                .collect(Collectors.toMap(User::getUserId, user -> 0L));

        // Count assignments for each user
        List<AreaAssignments> allAssignments = areaAssignmentsDao.findAll();
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

    /**
     * Get assignments by status (for Kanban board columns)
     */
    public List<AreaAssignments> getAssignmentsByStatus(com.hci.ufosightings.common.AssignmentStatus status) {
        return areaAssignmentsDao.findByStatus(status);
    }

    /**
     * Get assignments grouped by area ID for a specific status This ensures one
     * card per area, with all assigned users shown
     */
    public Map<Long, List<AreaAssignments>> getAssignmentsGroupedByArea(com.hci.ufosightings.common.AssignmentStatus status) {
        List<AreaAssignments> assignments = areaAssignmentsDao.findByStatus(status);
        Map<Long, List<AreaAssignments>> groupedByArea = new HashMap<>();

        for (AreaAssignments assignment : assignments) {
            Long areaId = assignment.getId().getAreaId();
            groupedByArea.computeIfAbsent(areaId, k -> new ArrayList<>()).add(assignment);
        }

        return groupedByArea;
    }

    /**
     * Update all assignments for an area to a new status This is called when
     * dragging a card between columns
     */
    public void updateAreaAssignmentsStatus(Long areaId, com.hci.ufosightings.common.AssignmentStatus newStatus) {
        List<AreaAssignments> allAssignments = areaAssignmentsDao.findAll();

        // Find all assignments for this area and update their status
        for (AreaAssignments assignment : allAssignments) {
            if (assignment.getId().getAreaId().equals(areaId)) {
                assignment.setStatus(newStatus);
                areaAssignmentsDao.save(assignment);
            }
        }
    }

    /**
     * Delete all assignments for an area (when moving to unassigned)
     */
    public void deleteAllAssignmentsForArea(Long areaId) {
        List<AreaAssignments> allAssignments = areaAssignmentsDao.findAll();

        // Find and delete all assignments for this area
        List<AreaAssignments> toDelete = allAssignments.stream()
                .filter(a -> a.getId().getAreaId().equals(areaId))
                .toList();

        areaAssignmentsDao.deleteAll(toDelete);
    }

    /**
     * Delete a single assignment (when removing user from area)
     */
    public void deleteAssignment(Long userId, Long areaId) {
        com.hci.ufosightings.common.AreaAssignmentsId id = new com.hci.ufosightings.common.AreaAssignmentsId();
        id.setUserId(userId);
        id.setAreaId(areaId);

        areaAssignmentsDao.deleteById(id);
    }

    /**
     * Create a new assignment for a user to an area Returns true if created,
     * false if already exists
     */
    public boolean createAssignment(Long userId, Long areaId, com.hci.ufosightings.common.AssignmentStatus status) {
        // Check if assignment already exists
        com.hci.ufosightings.common.AreaAssignmentsId id = new com.hci.ufosightings.common.AreaAssignmentsId();
        id.setUserId(userId);
        id.setAreaId(areaId);

        if (areaAssignmentsDao.existsById(id)) {
            return false;
        }

        // Fetch the user and area entities
        User user = userDao.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        Area area = areaDao.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found: " + areaId));

        // Create the assignment with all required fields
        AreaAssignments assignment = new AreaAssignments();
        assignment.setId(id);
        assignment.setUser(user);
        assignment.setArea(area);
        assignment.setAssignedAt(java.time.LocalDate.now());
        assignment.setStatus(status);

        // Save to database
        areaAssignmentsDao.save(assignment);
        return true;
    }
}
