package kata.security.controller;

import kata.security.model.User;
import kata.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

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
        log.info("Called method userInfo");

        User user = userService.findUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/index")
    public String homePage() {
        return "index";
    }

    @GetMapping("/test")
    public String test() {
        return "test message";
    }

    @GetMapping("/what")
    public ResponseEntity<Optional<User>> responseEntity (@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

}
