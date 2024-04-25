package com.hrms.sys.services.role;

import com.hrms.sys.models.Role;
import com.hrms.sys.repositories.RoleRepository;
import com.hrms.sys.services.role.IRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
