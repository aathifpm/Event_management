package com.college.eventmanagement.controller;

import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.service.UserService;
import com.college.eventmanagement.service.ClubService;
import com.college.eventmanagement.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private EventService eventService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Check if current user is admin
    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        return userOpt.isPresent() && userOpt.get().getRole() == Role.ADMIN;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return userService.getUserByEmail(auth.getName()).orElse(null);
    }

    // Admin Users List
    @GetMapping("/users")
    public String adminUsers(Model model, 
                           @RequestParam(value = "search", required = false) String search,
                           @RequestParam(value = "role", required = false) Role roleFilter,
                           @RequestParam(value = "status", required = false) String statusFilter,
                           RedirectAttributes redirectAttributes) {
        
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        List<User> users;
        
        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search.trim());
            model.addAttribute("searchTerm", search);
        } else if (roleFilter != null) {
            users = userService.getUsersByRole(roleFilter);
            model.addAttribute("roleFilter", roleFilter);
        } else if (statusFilter != null) {
            boolean isActive = "active".equals(statusFilter);
            users = userService.getUsersByStatus(isActive);
            model.addAttribute("statusFilter", statusFilter);
        } else {
            users = userService.getAllUsers();
        }

        // Get statistics
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", userService.getActiveUserCount());
        model.addAttribute("totalStudents", userService.getUserCountByRole(Role.STUDENT));
        model.addAttribute("totalClubHeads", userService.getUserCountByRole(Role.CLUB_HEAD));
        model.addAttribute("totalAdmins", userService.getUserCountByRole(Role.ADMIN));
        model.addAttribute("inactiveUsers", userService.getInactiveUserCount());
        model.addAttribute("roles", Role.values());
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("title", "User Management");

        return "admin/users";
    }

    // View User Details
    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/users";
        }

        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("userClubs", clubService.getClubsByMember(user));
        model.addAttribute("userEvents", eventService.getRegistrationsByUser(user));
        model.addAttribute("title", "User Details - " + user.getName());

        // If user is club head, get their clubs
        if (user.getRole() == Role.CLUB_HEAD) {
            model.addAttribute("managedClubs", clubService.getClubsByHead(user));
        }

        return "admin/user-details";
    }

    // Show Create User Form
    @GetMapping("/users/create")
    public String showCreateUserForm(Model model, RedirectAttributes redirectAttributes) {
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        model.addAttribute("title", "Create New User");
        model.addAttribute("isEdit", false);

        return "admin/user-form";
    }

    // Handle Create User
    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute User user, 
                           BindingResult result, 
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model, 
                           RedirectAttributes redirectAttributes) {
        
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Passwords do not match");
        }

        // Check if email already exists
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", "error.user", "Email already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("title", "Create New User");
            model.addAttribute("isEdit", false);
            return "admin/user-form";
        }

        try {
            // Encode password and set defaults
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setIsActive(true);

            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
            return "redirect:/admin/users";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
            return "redirect:/admin/users/create";
        }
    }

    // Show Edit User Form
    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/users";
        }

        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("title", "Edit User - " + user.getName());
        model.addAttribute("isEdit", true);

        return "admin/user-form";
    }

    // Handle Edit User
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute User userForm,
                           BindingResult result,
                           @RequestParam(value = "newPassword", required = false) String newPassword,
                           @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        Optional<User> existingUserOpt = userService.getUserById(id);
        if (existingUserOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/admin/users";
        }

        User existingUser = existingUserOpt.get();

        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(userForm.getEmail())) {
            if (userService.getUserByEmail(userForm.getEmail()).isPresent()) {
                result.rejectValue("email", "error.user", "Email already exists");
            }
        }

        // Check password confirmation if password is being changed
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                result.rejectValue("password", "error.user", "Passwords do not match");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("title", "Edit User - " + existingUser.getName());
            model.addAttribute("isEdit", true);
            return "admin/user-form";
        }

        try {
            // Update user fields
            existingUser.setName(userForm.getName());
            existingUser.setEmail(userForm.getEmail());
            existingUser.setDepartment(userForm.getDepartment());
            existingUser.setRole(userForm.getRole());
            existingUser.setIsActive(userForm.getIsActive());

            // Update password if provided
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newPassword));
            }

            userService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
            return "redirect:/admin/users/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
        }
    }

    // Toggle User Status
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/users";
            }

            User user = userOpt.get();
            user.setIsActive(!user.getIsActive());
            userService.saveUser(user);

            String status = user.getIsActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("success", "User " + status + " successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // Delete User
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isCurrentUserAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
            return "redirect:/dashboard";
        }

        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/users";
            }

            User currentUser = getCurrentUser();
            User userToDelete = userOpt.get();

            // Prevent admin from deleting themselves
            if (currentUser != null && currentUser.getUserId().equals(userToDelete.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "You cannot delete your own account");
                return "redirect:/admin/users";
            }

            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
