package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByDepartment(String department);
    
    List<User> findByIsActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% AND u.isActive = true")
    List<User> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countByRoleAndIsActiveTrue(@Param("role") Role role);
    
    @Query("SELECT u FROM User u WHERE u.department = :department AND u.role = :role AND u.isActive = true")
    List<User> findByDepartmentAndRoleAndIsActiveTrue(@Param("department") String department, @Param("role") Role role);
}
