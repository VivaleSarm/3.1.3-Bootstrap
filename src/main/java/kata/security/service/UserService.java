package kata.security.service;

import kata.security.model.Role;
import kata.security.model.User;
import kata.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return repository.findById(id);
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public User findUserByEmail(String username) {
        return repository.findByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof String) {
            return findUserByEmail((String) principal);
        }

        throw new IllegalStateException(
                "Unknown principal type: " + principal.getClass()
        );
    }

    public User addUser(String firstName,
                        String lastName,
                        int age,
                        String email,
                        String password,
                        List<Long> roles) {
        log.info("Creating new user: email={}", email);

        User user = new User();
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        if (roles != null && !roles.isEmpty()) {
            List<Role> roleList = roleService.findByIds(roles);
            user.setRoles(new HashSet<>(roleList));
        }
        return repository.save(user);
    }

    public void editUser(Long id,
                         String firstName,
                         String lastName,
                         int age,
                         String email,
                         String password,
                         List<Long> roles) {
        log.info("Editing user: id={}, email={}", id, email);

        Optional<User> userOptional = findUserById(id);
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
            save(user);
        }
    }
}
