package com.college.eventmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Relationships
    @OneToMany(mappedBy = "head", cascade = CascadeType.ALL)
    private List<Club> managedClubs;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ClubMember> clubMemberships;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventRegistration> eventRegistrations;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String name, String email, String password, Role role, String department) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<Club> getManagedClubs() {
        return managedClubs;
    }
    
    public void setManagedClubs(List<Club> managedClubs) {
        this.managedClubs = managedClubs;
    }
    
    public List<ClubMember> getClubMemberships() {
        return clubMemberships;
    }
    
    public void setClubMemberships(List<ClubMember> clubMemberships) {
        this.clubMemberships = clubMemberships;
    }
    
    public List<EventRegistration> getEventRegistrations() {
        return eventRegistrations;
    }
    
    public void setEventRegistrations(List<EventRegistration> eventRegistrations) {
        this.eventRegistrations = eventRegistrations;
    }
    
    public List<Notification> getNotifications() {
        return notifications;
    }
    
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
