package kata.security.mapper;

import kata.security.model.Role;
import kata.security.service.RoleService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapperHelper {

    private final RoleService roleService;

    public UserMapperHelper(RoleService roleService) {
        this.roleService = roleService;
    }

    public Set<Role> idsToRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Role> roles = roleService.findByIds(roleIds);

        return new HashSet<>(roles);
    }

    public List<String> rolesToNames(Set<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    public List<Long> rolesToIds(Set<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
    }
}
