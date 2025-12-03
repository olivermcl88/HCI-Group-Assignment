package com.hci.ufosightings.web;

import com.hci.ufosightings.common.Comment;
import com.hci.ufosightings.common.Sighting;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dto.CommentWithUser;
import com.hci.ufosightings.service.AreaService;
import com.hci.ufosightings.service.CommentService;
import com.hci.ufosightings.service.SightingService;
import com.hci.ufosightings.service.TeamService;
import com.hci.ufosightings.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HelloWorldController {

    private final UserService userService;
    private final AreaService areaService;
    private final TeamService teamService;
    private final SightingService sightingService;
    private final CommentService commentService;

    // A simple controller to return "Hello, World!" message and lists
    @GetMapping("hello-world")
    public String helloWorld(Model model) {
        // call services and add lists to the model
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("areas", areaService.getAllAreas());
        model.addAttribute("teams", teamService.getAllTeams());

        return "hello-world";
    }

    // Controller for the sightings page
    @GetMapping("sightings")
    public String sightings(Model model) {
        List<Sighting> allSightings = sightingService.getAllSightings();
        model.addAttribute("sightings", allSightings);
        
        // If there are sightings, show the first one by default
        if (!allSightings.isEmpty()) {
            Sighting firstSighting = allSightings.get(0);
            model.addAttribute("currentSighting", firstSighting);
            
            // Get reporter information
            User reporter = userService.getUserById(firstSighting.getReporterUserId());
            model.addAttribute("reporter", reporter);
            
            // Get comments for this sighting
            List<CommentWithUser> comments = commentService.getCommentsBySightingId(firstSighting.getSightingId());
            model.addAttribute("comments", comments);
        }
        
        return "sightings";
    }
    
    // Controller for viewing a specific sighting
    @GetMapping("sightings/{id}")
    public String viewSighting(@PathVariable Long id, Model model) {
        List<Sighting> allSightings = sightingService.getAllSightings();
        model.addAttribute("sightings", allSightings);
        
        Optional<Sighting> sighting = sightingService.getSightingById(id);
        if (sighting.isPresent()) {
            model.addAttribute("currentSighting", sighting.get());
            
            // Get reporter information
            User reporter = userService.getUserById(sighting.get().getReporterUserId());
            model.addAttribute("reporter", reporter);
            
            // Get comments for this sighting
            List<CommentWithUser> comments = commentService.getCommentsBySightingId(id);
            model.addAttribute("comments", comments);
        } else {
            // If sighting not found, redirect to general sightings page
            return "redirect:/ufo-app/sightings";
        }
        
        return "sightings";
    }
    
    // Controller for voting on a sighting
    @PostMapping("sightings/{id}/vote")
    public String voteOnSighting(@PathVariable Long id, @RequestParam String voteType) {
        sightingService.voteOnSighting(id, voteType);
        return "redirect:/ufo-app/sightings/" + id;
    }
    
    // Controller for adding a comment
    @PostMapping("sightings/{id}/comment")
    public String addComment(@PathVariable Long id, 
                           @RequestParam String commentText,
                           @RequestParam(defaultValue = "1") Long userId,
                           @RequestParam(required = false) Boolean isAnonymous) {
        
        if (commentText != null && !commentText.trim().isEmpty()) {
            commentService.addComment(id, userId, commentText.trim(), isAnonymous);
        }
        
        return "redirect:/ufo-app/sightings/" + id;
    }

}
