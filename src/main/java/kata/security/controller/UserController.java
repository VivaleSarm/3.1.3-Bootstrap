package kata.security.controller;

import kata.security.dto.UserDto;
import kata.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String userInfo(@AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        log.info("Called userInfo method from UserController for user: {}", userDetails.getUsername());

        try {
            UserDto currentUser = userService.getCurrentUser();
            model.addAttribute("user", currentUser);

            log.debug("Profile loaded successfully for user: {}", currentUser.getEmail());
        } catch (Exception e) {
            log.error("Error loading profile for user: {}", userDetails.getUsername(), e);
            model.addAttribute("error", "Could not load user profile");
        }

        return "user/profile";
    }
}
