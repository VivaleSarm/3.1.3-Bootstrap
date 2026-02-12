package kata.security.controller;


import kata.security.model.Role;
import kata.security.model.User;
import kata.security.service.RoleService;
import kata.security.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final RoleService roleService;

    public RegistrationController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String registerForm(Model model) {
        User user = new User();
        model.addAttribute(user);
        model.addAttribute("allRoles", roleService.findAll());

        model.addAttribute("selectedRoleIds", new ArrayList<Long>());

        return "public/register";
    }

    @PostMapping
    public String register(Model model, User user, @RequestParam("selectedRoleIds") List<Long> selectedRoleIds) {
        List<Role> rolesList = roleService.findByIds(selectedRoleIds);

        Set<Role> rolesSet = new HashSet<>(rolesList);
        user.setRoles(rolesSet);

        userService.registerUser(user);

        return "redirect:/login?registered";
    }

}
