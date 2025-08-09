package com.college.eventmanagement.config;

import com.college.eventmanagement.model.User;
import com.college.eventmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserAttributes(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                
                String userEmail = authentication.getName();
                User currentUser = userService.getUserByEmail(userEmail).orElse(null);
                
                if (currentUser != null) {
                    model.addAttribute("currentUser", currentUser);
                    model.addAttribute("isAuthenticated", true);
                    model.addAttribute("userRole", currentUser.getRole().name());
                    model.addAttribute("isAdmin", currentUser.getRole().name().equals("ADMIN"));
                    model.addAttribute("isClubHead", currentUser.getRole().name().equals("CLUB_HEAD"));
                    model.addAttribute("isStudent", currentUser.getRole().name().equals("STUDENT"));
                } else {
                    model.addAttribute("isAuthenticated", false);
                }
            } else {
                model.addAttribute("isAuthenticated", false);
            }
        } catch (Exception e) {
            // If there's any issue with authentication, default to not authenticated
            model.addAttribute("isAuthenticated", false);
        }
    }
}
