package com.hci.ufosightings.web;

import com.hci.ufosightings.service.AreaService;
import com.hci.ufosightings.service.TeamService;
import com.hci.ufosightings.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HelloWorldController {

    private final UserService userService;

    private final AreaService areaService;

    private final TeamService teamService;

    // A simple controller to return "Hello, World!" message and lists
    @GetMapping("hello-world")
    public String helloWorld(Model model) {
        // call services and add lists to the model
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("areas", areaService.getAllAreas());
        model.addAttribute("teams", teamService.getAllTeams());

        return "hello-world";
    }

}
