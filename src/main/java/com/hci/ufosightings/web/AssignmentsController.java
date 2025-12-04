package com.hci.ufosightings.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.common.AssignmentStatus;
import com.hci.ufosightings.service.AreaAssignmentsService;
import com.hci.ufosightings.service.AreaService;
import com.hci.ufosightings.service.TeamService;
import com.hci.ufosightings.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AssignmentsController {

    private final UserService userService;

    private final AreaService areaService;

    private final TeamService teamService;

    private final AreaAssignmentsService areaAssignmentsService;

    @GetMapping("assignments")
    public String assignments(Model model) {
        // call services and add lists to the model
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("areas", areaService.getAllAreas());
        model.addAttribute("teams", teamService.getAllTeams());

        // Assignment data - both counts and full details
        model.addAttribute("assignmentCounts", areaAssignmentsService.getAssignmentCountsForAllUsers());
        model.addAttribute("userAssignments", areaAssignmentsService.getAssignmentsGroupedByUser());

        // Kanban board data - grouped by area (one card per area, showing all users)
        model.addAttribute("unassignedAreas", areaService.getUnassignedAreas());
        model.addAttribute("pendingByArea", areaAssignmentsService.getAssignmentsGroupedByArea(com.hci.ufosightings.common.AssignmentStatus.PENDING));
        model.addAttribute("activeByArea", areaAssignmentsService.getAssignmentsGroupedByArea(com.hci.ufosightings.common.AssignmentStatus.ACTIVE));

        return "assignments";
    }

    /**
     * REST API endpoint to update assignment status when cards are dragged
     */
    @PostMapping("/api/update-assignment-status")
    @ResponseBody
    public ResponseEntity<String> updateAssignmentStatus(
            @RequestParam Long areaId,
            @RequestParam String targetColumn) {

        try {
            switch (targetColumn) {
                case "unassigned":
                    // Remove all assignments for this area
                    areaAssignmentsService.deleteAllAssignmentsForArea(areaId);
                    break;

                case "pending":
                    // Update all assignments to PENDING status
                    areaAssignmentsService.updateAreaAssignmentsStatus(areaId, AssignmentStatus.PENDING);
                    break;

                case "active":
                    // Update all assignments to ACTIVE status
                    areaAssignmentsService.updateAreaAssignmentsStatus(areaId, AssignmentStatus.ACTIVE);
                    break;

                default:
                    return ResponseEntity.badRequest().body("Invalid target column: " + targetColumn);
            }

            return ResponseEntity.ok("SUCCESS");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * REST API endpoint to get updated assignment counts for all users Used to
     * update the sidebar dynamically
     */
    @GetMapping("/api/assignment-counts")
    @ResponseBody
    public ResponseEntity<Map<Long, Long>> getAssignmentCounts() {
        try {
            Map<Long, Long> counts = areaAssignmentsService.getAssignmentCountsForAllUsers();
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * REST API endpoint to assign users to an unassigned area
     */
    @PostMapping("/api/assign-users-to-area")
    @ResponseBody
    public ResponseEntity<String> assignUsersToArea(
            @RequestParam Long areaId,
            @RequestParam String userIds,
            @RequestParam String status) {

        try {

            // Parse user IDs
            String[] userIdArray = userIds.split(",");

            // Convert status string to enum
            AssignmentStatus assignmentStatus;
            try {
                assignmentStatus = AssignmentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status: " + status);
            }

            // Create assignments for each user
            int created = 0;
            int skipped = 0;
            for (String userIdStr : userIdArray) {
                try {
                    Long userId = Long.parseLong(userIdStr.trim());
                    boolean wasCreated = areaAssignmentsService.createAssignment(userId, areaId, assignmentStatus);
                    if (wasCreated) {
                        created++;
                    } else {
                        skipped++;
                    }
                } catch (NumberFormatException e) {
                    log.error("ERROR: Invalid user ID: {}", userIdStr);
                } catch (Exception e) {
                    log.error("ERROR: Failed to create assignment for user {}", userIdStr, e);
                }
            }
            return ResponseEntity.ok("SUCCESS: Created " + created + " assignments"
                    + (skipped > 0 ? " (" + skipped + " already assigned)" : ""));

        } catch (Exception e) {
            log.error("ERROR: Exception assigning users to area {}", areaId, e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * REST API endpoint to add a single user to an area Used when dragging a
     * team member from sidebar to a card
     */
    @PostMapping("/api/add-user-to-area")
    @ResponseBody
    public ResponseEntity<String> addUserToArea(
            @RequestParam Long userId,
            @RequestParam Long areaId,
            @RequestParam String status) {

        try {

            // Convert status string to enum
            AssignmentStatus assignmentStatus;
            try {
                assignmentStatus = AssignmentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status: " + status);
            }

            // Create assignment for this user (returns false if already exists)
            boolean created = areaAssignmentsService.createAssignment(userId, areaId, assignmentStatus);

            if (created) {
                return ResponseEntity.ok("SUCCESS");
            } else {
                return ResponseEntity.ok("ALREADY_ASSIGNED");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * REST API endpoint to remove a user from an area
     */
    @PostMapping("/api/remove-user-from-area")
    @ResponseBody
    public ResponseEntity<String> removeUserFromArea(
            @RequestParam Long userId,
            @RequestParam Long areaId) {

        try {
            areaAssignmentsService.deleteAssignment(userId, areaId);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            log.error("ERROR: Exception removing user from area", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * REST API endpoint to change area priority
     */
    @PostMapping("/api/update-area-priority")
    @ResponseBody
    public ResponseEntity<String> updateAreaPriority(
            @RequestParam Long areaId,
            @RequestParam String priority) {

        try {
            areaService.updateAreaPriority(areaId, priority);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            log.error("ERROR: Exception updating area priority", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * REST API endpoint to get area details with assignments
     */
    @GetMapping("/api/area-details/{areaId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAreaDetails(@PathVariable Long areaId) {
        try {
            Map<String, Object> areaDetails = areaService.getAreaDetailsWithAssignments(areaId);
            return ResponseEntity.ok(areaDetails);
        } catch (Exception e) {
            log.error("ERROR: Exception getting area details", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * REST API endpoint to get assignments for a specific user Returns fresh
     * data from database when modal is opened
     */
    @GetMapping("/api/user-assignments/{userId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getUserAssignments(@PathVariable Long userId) {
        try {
            List<AreaAssignments> assignments = areaAssignmentsService.getAssignmentsByUserId(userId);

            // Convert to simple maps to avoid JSON circular reference issues
            List<Map<String, Object>> result = assignments.stream()
                    .map(assignment -> {
                        Map<String, Object> assignmentMap = new HashMap<>();
                        assignmentMap.put("status", assignment.getStatus().toString());
                        assignmentMap.put("assignedAt", assignment.getAssignedAt().toString());

                        Map<String, Object> areaMap = new HashMap<>();
                        areaMap.put("areaId", assignment.getArea().getAreaId());
                        areaMap.put("areaName", assignment.getArea().getAreaName());
                        areaMap.put("description", assignment.getArea().getDescription());
                        areaMap.put("areaPriority", assignment.getArea().getAreaPriority().toString());

                        assignmentMap.put("area", areaMap);
                        return assignmentMap;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
