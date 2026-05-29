package kata.security.service;

import kata.security.dto.UserDto;
import kata.security.mapper.UserMapper;
import kata.security.mapper.UserMapperHelper;
import kata.security.model.Role;
import kata.security.model.User;
import kata.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final UserMapper userMapper;
    private final UserMapperHelper userMapperHelper;

    @Autowired
    public UserService(UserRepository repository, RoleService roleService, BCryptPasswordEncoder passwordEncoder, UserMapper userMapper, UserMapperHelper userMapperHelper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userMapperHelper = userMapperHelper;
    }

    public List<UserDto> findAllUsers() {
        log.info("Finding all users");

        List<User> users = repository.findAllWithRoles();
        List<UserDto> userDtos = userMapper.toDtoList(users);

        return userDtos;
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    public void deleteById(Long id) {
        log.info("Deleting user: id={}", id);

        if (!repository.existsById(id)) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("User deleted successfully: id={}", id);

    }

    public UserDto getCurrentUser() {
        log.info("Getting current authenticated  user");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        String email;

        if (principal instanceof User) {
            email = ((User) principal).getEmail();
        } else if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new IllegalStateException(
                    "Unknown principal type: " + principal.getClass().getName()
            );
        }

        User user = repository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return userMapper.toDto(user);

    }

    public UserDto addUser(UserDto userDto) {
        log.info("Creating new user: email={}", userDto.getEmail());

        if (repository.findByEmail(userDto.getEmail()) != null) {
            throw new IllegalArgumentException(
                    "User with email " + userDto.getEmail() + " already exists"
            );
        }

        User user = userMapper.toEntity(userDto);

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(encodedPassword);

        if (userDto.getRoleIds() != null && !userDto.getRoleIds().isEmpty()) {
            Set<Role> roleSet = userMapperHelper.idsToRoles(userDto.getRoleIds());
            user.setRoles(roleSet);
        }

        User savedUser = repository.save(user);
        log.info("User created successfully: id={}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    public UserDto editUser(Long id, UserDto userDto) {
        log.info("Editing user: id={}, email={}", id, userDto.getEmail());

        User user = repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (!user.getEmail().equals(userDto.getEmail())) {
            User existingUser = repository.findByEmail(userDto.getEmail());
            if (existingUser != null) {
                throw new IllegalArgumentException(
                        "Email " + userDto.getEmail() + " is already exist"
                );
            }
        }

        userMapper.updateEntity(userDto, user);

        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            user.setPassword(encodedPassword);
            log.info("Password updated for user id={}", id);
        }

        if (userDto.getRoleIds() != null) {
            Set<Role> roles = userMapperHelper.idsToRoles(userDto.getRoleIds());
            user.setRoles(roles);
            log.debug("Roles updated for user id={}", id);
        }

        User updatedUser = repository.save(user);
        log.info("User updated successfully: id={}", updatedUser.getId());

        return userMapper.toDto(updatedUser);
    }

}
