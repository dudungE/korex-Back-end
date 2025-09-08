package com.project.korex.user.repository.jpa;

import com.project.korex.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

}
