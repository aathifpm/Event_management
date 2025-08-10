package com.college.eventmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.service.ClubService;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.UserService;

@Controller
public class DashboardController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail).orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Add user info to model
        model.addAttribute("currentUser", currentUser);
        
        // Dashboard statistics
        model.addAttribute("totalEvents", eventService.getTotalEventCount());
        model.addAttribute("activeClubs", clubService.getActiveClubCount());
        model.addAttribute("totalUsers", userService.getActiveUserCount());
        model.addAttribute("upcomingEvents", eventService.getUpcomingEventCount());

        // Get upcoming events (limit to 5 for dashboard)
        List<Event> upcomingEventsList = eventService.getUpcomingEvents();
        if (upcomingEventsList.size() > 5) {
            upcomingEventsList = upcomingEventsList.subList(0, 5);
        }
        model.addAttribute("upcomingEventsList", upcomingEventsList);

        // Role-specific data
        switch (currentUser.getRole()) {
            case ADMIN -> {
                // Admin dashboard data
                model.addAttribute("totalStudents", userService.getUserCountByRole(Role.STUDENT));
                model.addAttribute("totalClubHeads", userService.getUserCountByRole(Role.CLUB_HEAD));
                model.addAttribute("pendingEvents", eventService.getPendingEventCount());
                model.addAttribute("allEvents", eventService.getAllEvents());
            }
            case CLUB_HEAD -> {
                // Club Head dashboard data
                model.addAttribute("myClubs", clubService.getClubsByHead(currentUser));
                model.addAttribute("myClubEvents", eventService.getEventsByClubHead(currentUser));
                model.addAttribute("myClubMembers", clubService.getTotalMembersByHead(currentUser));
            }
            case STUDENT -> {
                // Student dashboard data
                model.addAttribute("myRegistrations", eventService.getRegistrationsByUser(currentUser));
                model.addAttribute("myClubs", clubService.getClubsByMember(currentUser));
                model.addAttribute("recommendedEvents", eventService.getRecommendedEvents(currentUser));
            }
        }

        return "dashboard/index";
    }
}
