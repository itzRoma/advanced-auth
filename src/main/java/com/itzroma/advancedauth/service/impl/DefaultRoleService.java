package com.itzroma.advancedauth.service.impl;

import com.itzroma.advancedauth.exception.EntityExistsException;
import com.itzroma.advancedauth.exception.EntityNotFoundException;
import com.itzroma.advancedauth.model.Role;
import com.itzroma.advancedauth.repository.RoleRepository;
import com.itzroma.advancedauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultRoleService implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw EntityExistsException.withField(Role.class, "role name", role.getRoleName().name());
        }
        return roleRepository.save(role);
    }

    @Override
    public Role findByRoleName(Role.RoleName roleName) {
        return roleRepository.findByRoleName(roleName).orElseThrow(
                () -> EntityNotFoundException.withField(Role.class, "role name", roleName.name())
        );
    }
}
