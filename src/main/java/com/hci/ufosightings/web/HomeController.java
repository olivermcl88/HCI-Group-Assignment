package com.hci.ufosightings.web;

import com.hci.ufosightings.common.User;
import com.hci.ufosightings.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // For now use user id 1 as the logged-in user
        User currentUser = userService.getUserById(1L);
        model.addAttribute("currentUser", currentUser);
        return "index";
    }
}

