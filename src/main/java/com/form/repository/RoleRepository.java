package com.form.repository;

import com.form.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    // Rol adına göre (Örn: "ROLE_ADMIN", "ROLE_TECHNICIAN") arama yapan kritik metot
    Optional<Role> findByName(String name);
}