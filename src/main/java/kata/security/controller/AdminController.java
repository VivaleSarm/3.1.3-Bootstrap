package kata.security.controller;


import kata.security.model.Role;
import kata.security.model.User;
import kata.security.service.RoleService;
import kata.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        return "admin/all_users";
    }

    @GetMapping("/user-create")
    public String createUserForm(Model model) {
        User user = new User();
        model.addAttribute(user);
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("selectedRoleIds", new ArrayList<Long>());
        return "admin/create";
    }

    @PostMapping("/user-create")
    public String createUser(Model model, User user, @RequestParam("selectedRoleIds") List<Long> selectedRoleIds) {

        List<Role> rolesList = roleService.findByIds(selectedRoleIds);

        Set<Role> rolesSet = new HashSet<>(rolesList);
        user.setRoles(rolesSet);

        userService.registerUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
        }
        model.addAttribute("allRoles", roleService.findAll());

        model.addAttribute("selectedRoleIds", new ArrayList<Long>());
        return "admin/edit";
    }

    @PostMapping("/update-user/{id}")
    public String updateUser(@PathVariable Long id, Model model, User user, @RequestParam("selectedRoleIds") List<Long> selectedRoleIds) {
        user.setId(id);

        if (selectedRoleIds != null) {
            List<Role> roles = roleService.findByIds(selectedRoleIds);
            user.setRoles(new HashSet<>(roles));
        }

        userService.registerUser(user);
        return "redirect:/user";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("Called method deleteUser: id={}", id);
        userService.deleteById(id);
        return "redirect:/admin";
    }



}
