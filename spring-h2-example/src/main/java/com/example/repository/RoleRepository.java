package com.example.repository;

import com.example.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author fdrama
 * date 2023年06月28日 17:03
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
}