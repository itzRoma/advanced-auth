package com.itzroma.advancedauth.repository;

import com.itzroma.advancedauth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleName(Role.RoleName roleName);

    Optional<Role> findByRoleName(Role.RoleName roleName);
}
