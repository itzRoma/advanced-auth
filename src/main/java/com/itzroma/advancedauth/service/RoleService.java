package com.itzroma.advancedauth.service;

import com.itzroma.advancedauth.model.Role;

public interface RoleService {
    Role save(Role role);

    Role findByRoleName(Role.RoleName roleName);
}
