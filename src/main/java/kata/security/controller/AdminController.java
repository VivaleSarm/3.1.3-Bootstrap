package kata.security.controller;


import kata.security.model.User;
import kata.security.service.RoleService;
import kata.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping
    public String allUsers(Model model) {

        log.info("Called allUsers method");
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("allRoles", roleService.findAll());

        return "admin/admin";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam int age,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam(required = false) List<Long> roles) {

        log.info("Called addUser method from AdminController for user: email{}", email);
        userService.addUser(firstName, lastName, age, email, password, roles);

        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam Long id,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam int age,
                           @RequestParam String email,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) List<Long> roles) {

        log.info("Called method editUser from AdminController for user email: {}", email);
        userService.editUser(id, firstName, lastName, age, email, password, roles);

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {

        log.info("Deleting user: id={}", id);
        userService.deleteById(id);

        return "redirect:/admin";
    }

}
