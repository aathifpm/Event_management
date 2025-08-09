package com.college.eventmanagement.controller;

import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(defaultValue = "robotics@college.edu") String email, 
                               @RequestParam(defaultValue = "Admin123") String newPassword) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String hashedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(hashedPassword);
                userRepository.save(user);
                
                return "Password reset successful for " + email + 
                       "<br>New password: " + newPassword + 
                       "<br>Hash: " + hashedPassword +
                       "<br><br><a href='/login'>Go to Login</a>";
            } else {
                return "User not found: " + email;
            }
        } catch (Exception e) {
            return "Error resetting password: " + e.getMessage();
        }
    }

    @GetMapping("/create-test-user")
    public String createTestUser() {
        try {
            // Check if user already exists
            if (userRepository.findByEmail("test@college.edu").isPresent()) {
                return "Test user already exists. <a href='/login'>Go to Login</a>";
            }

            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("test@college.edu");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setRole(com.college.eventmanagement.model.Role.STUDENT);
            testUser.setDepartment("Computer Science");
            testUser.setIsActive(true);
            testUser.setCreatedAt(java.time.LocalDateTime.now());

            userRepository.save(testUser);

            return "Test user created successfully!" +
                   "<br>Email: test@college.edu" +
                   "<br>Password: test123" +
                   "<br><br><a href='/login'>Go to Login</a>";
        } catch (Exception e) {
            return "Error creating test user: " + e.getMessage();
        }
    }
}
