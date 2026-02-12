package kata.security.service;

import kata.security.model.Role;
import kata.security.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public List<Role> findByIds(List<Long> selectedRoleIds) {
        return roleRepository.findAllById(selectedRoleIds);
    }
}
