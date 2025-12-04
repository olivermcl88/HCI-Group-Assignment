package com.hci.ufosightings.web;

import com.hci.ufosightings.service.AreaAssignmentsService;
import com.hci.ufosightings.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReporterDashBoardController {

    private final NotificationService notificationService;
    private final AreaAssignmentsService areaAssignmentsService;

    @GetMapping("/reporter-dashboard/{userId}")
    public String reporterDashboard(Model model, @PathVariable Long userId) {

        model.addAttribute("areaAssignments", areaAssignmentsService.getAssignmentsByUserId(userId));

        // placeholders for features not yet implemented
        model.addAttribute("activities", Collections.emptyList());
        model.addAttribute("chatLog", Collections.emptyList());

        model.addAttribute("notifications", notificationService.getAllNotisByUserId(userId));

        return "reporter-dashboard";
    }

}
