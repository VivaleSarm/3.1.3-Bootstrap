package kata.security.controller;


import kata.security.model.Role;
import kata.security.model.User;
import kata.security.service.RoleService;
import kata.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                user = (User) principal;
            } else if (principal instanceof String) {
                String email = (String) principal;
                user = userService.findUserByEmail(email);
            }
        }

        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());

        return "admin/admin";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam int age,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam(required = false) List<Long> roles) { //long?
        log.info("Adding new user: email={}", email);

        User user = new User();
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(password);

        if (roles != null && !roles.isEmpty()) {
            List<Role> roleList = roleService.findByIds(roles);
            user.setRoles(new HashSet<>(roleList));
        }
        userService.save(user);

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
        log.info("Editing user: id={}, email={}", id, email);

        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setAge(age);
            user.setEmail(email);

            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }

            if (roles != null && !roles.isEmpty()) {
                List<Role> roleList = roleService.findByIds(roles);
                user.setRoles(new HashSet<>(roleList));
            }

            userService.registerUser(user);
        }

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        log.info("Deleting user: id={}", id);

        userService.deleteById(id);
        return "redirect:/admin";
    }

/*

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

*/


}
