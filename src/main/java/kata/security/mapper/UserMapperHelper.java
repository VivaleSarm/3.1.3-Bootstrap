package kata.security.mapper;

import kata.security.model.Role;
import kata.security.service.RoleService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

}
