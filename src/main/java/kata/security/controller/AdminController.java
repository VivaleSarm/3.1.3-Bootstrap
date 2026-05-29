package kata.security.controller;


import kata.security.dto.UserDto;
import kata.security.service.RoleService;
import kata.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        List<UserDto> users = userService.findAllUsers();
        UserDto currentUser = userService.getCurrentUser();

        model.addAttribute("users", users);
        model.addAttribute("user", currentUser);
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("userDto", new UserDto());

        return "admin/admin";
    }

    @PostMapping("/add")
    public String addUser(@Validated(UserDto.Create.class)
                          @ModelAttribute("userDto") UserDto userDto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        log.info("Called addUser method from AdminController for user: email{}", userDto.getEmail());

        if (result.hasErrors()) {
            log.warn("Validation failed: {}", result.getAllErrors());
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("user", userService.getCurrentUser());
            model.addAttribute("allRoles", roleService.findAll());
            return "admin/admin";
        }

        try {
            UserDto createdUser = userService.addUser(userDto);
            log.info("User created: id={}", createdUser.getId());
            redirectAttributes.addFlashAttribute("success", "User created successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute UserDto userDto,
                           RedirectAttributes redirectAttributes) {
        log.info("Called method editUser from AdminController for user: email={}", userDto.getEmail());

        try {
            userService.editUser(userDto.getId(), userDto);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id,
                             RedirectAttributes redirectAttributes) {
        log.info("Deleting user: id={}", id);

        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Failed to delete user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin";
    }

}
