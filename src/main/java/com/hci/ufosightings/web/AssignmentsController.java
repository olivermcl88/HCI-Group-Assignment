package com.hci.ufosightings.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    // A simple controller to return "Hello, World!" message and lists
    @GetMapping("assignments")
    public String assignments(Model model) {
        // call services and add lists to the model
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("areas", areaService.getAllAreas());
        model.addAttribute("teams", teamService.getAllTeams());

        // Assignment data - both counts and full details
        model.addAttribute("assignmentCounts", areaAssignmentsService.getAssignmentCountsForAllUsers());
        model.addAttribute("userAssignments", areaAssignmentsService.getAssignmentsGroupedByUser());

        return "assignments";
    }

}
