package com.college.eventmanagement.controller;

import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "College Event Management System");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("studentId") String studentId,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("department") String department,
            @RequestParam("year") String year,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }
            
            // Check if user already exists
            if (userRepository.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email already registered");
                return "redirect:/register";
            }
            
            // Create new user
            User user = new User();
            user.setName(firstName + " " + lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(Role.STUDENT); // Default role for registration
            user.setDepartment(department);
            user.setCreatedAt(LocalDateTime.now());
            user.setIsActive(true);
            
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed. Please try again.");
            return "redirect:/register";
        }
    }
}
