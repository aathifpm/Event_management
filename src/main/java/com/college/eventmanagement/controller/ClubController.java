package com.college.eventmanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.ClubMember;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.service.ClubService;
import com.college.eventmanagement.service.ClubMemberService;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    // List all clubs
    @GetMapping
    public String listClubs(Model model, 
                          @RequestParam(value = "search", required = false) String search) {
        
        List<Club> clubs;
        
        if (search != null && !search.trim().isEmpty()) {
            clubs = clubService.searchClubs(search);
        } else {
            clubs = clubService.getAllClubs();
        }
        
        model.addAttribute("clubs", clubs);
        model.addAttribute("search", search);
        model.addAttribute("title", "All Clubs");
        
        return "clubs/list";
    }

    // Display club details
    @GetMapping("/{id}")
    public String clubDetails(@PathVariable Long id, Model model) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            return "redirect:/clubs?error=Club not found";
        }
        
        Club club = clubOpt.get();
        
        // Get recent members and events using service methods to avoid lazy loading
        List<ClubMember> recentMembers = clubMemberService.getActiveMembersByClubWithUser(club.getClubId());
        List<Event> recentEvents = eventService.getEventsByClub(club.getClubId());
        
        // Limit to first 6 items for preview
        if (recentMembers.size() > 6) {
            recentMembers = recentMembers.subList(0, 6);
        }
        if (recentEvents.size() > 6) {
            recentEvents = recentEvents.subList(0, 6);
        }
        
        model.addAttribute("club", club);
        model.addAttribute("recentMembers", recentMembers);
        model.addAttribute("recentEvents", recentEvents);
        model.addAttribute("memberCount", clubMemberService.getActiveMemberCount(club));
        model.addAttribute("eventCount", recentEvents.size());
        model.addAttribute("title", club.getClubName());
        
        // Get current user for role-based checks
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String userEmail = authentication.getName();
            User currentUser = userService.getUserByEmail(userEmail).orElse(null);
            
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("isClubHead", clubService.isUserClubHead(currentUser, club));
            }
        }
        
        return "clubs/details";
    }

    // Display club members
    @GetMapping("/{id}/members")
    public String clubMembers(@PathVariable Long id, Model model) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            return "redirect:/clubs?error=Club not found";
        }
        
        Club club = clubOpt.get();
        List<ClubMember> members = clubMemberService.getActiveMembersByClubWithUser(club.getClubId());
        
        model.addAttribute("club", club);
        model.addAttribute("members", members);
        model.addAttribute("title", club.getClubName() + " - Members");
        
        // Get current user for role-based checks
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String userEmail = authentication.getName();
            User currentUser = userService.getUserByEmail(userEmail).orElse(null);
            
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("isClubHead", clubService.isUserClubHead(currentUser, club));
            }
        }
        
        return "clubs/members";
    }

    // Display club events
    @GetMapping("/{id}/events")
    public String clubEvents(@PathVariable Long id, Model model) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            return "redirect:/clubs?error=Club not found";
        }
        
        Club club = clubOpt.get();
        List<Event> events = eventService.getEventsByClub(club.getClubId());
        
        // Get current user for role-based checks
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String userEmail = authentication.getName();
            User currentUser = userService.getUserByEmail(userEmail).orElse(null);
            
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("isClubHead", clubService.isUserClubHead(currentUser, club));
            }
        }
        
        model.addAttribute("club", club);
        model.addAttribute("events", events);
        model.addAttribute("title", club.getClubName() + " - Events");
        
        return "clubs/events";
    }

    // Show create club form (for authenticated users)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("club", new Club());
        model.addAttribute("title", "Create New Club");
        return "clubs/create";
    }

    // Handle club creation
    @PostMapping("/create")
    public String createClub(@Valid @ModelAttribute Club club, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "clubs/create";
        }
        
        if (clubService.existsByName(club.getClubName())) {
            result.rejectValue("clubName", "error.club", "A club with this name already exists");
            return "clubs/create";
        }
        
        try {
            // Get current user as club head
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                Optional<User> userOpt = userService.getUserByEmail(auth.getName());
                if (userOpt.isPresent()) {
                    club.setHead(userOpt.get());
                }
            }
            
            Club savedClub = clubService.saveClub(club);
            redirectAttributes.addFlashAttribute("success", "Club created successfully!");
            return "redirect:/clubs/" + savedClub.getClubId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating club: " + e.getMessage());
            return "redirect:/clubs/create";
        }
    }

    // Join club (for authenticated users)
    @PostMapping("/{id}/join")
    public String joinClub(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("error", "Please login to join a club");
                return "redirect:/login";
            }
            
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            Optional<Club> clubOpt = clubService.getClubById(id);
            
            if (userOpt.isEmpty() || clubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User or club not found");
                return "redirect:/clubs";
            }
            
            User user = userOpt.get();
            Club club = clubOpt.get();
            
            // Check if user is already a member
            if (clubMemberService.isUserMemberOfClub(club, user)) {
                redirectAttributes.addFlashAttribute("error", "You are already a member of this club");
                return "redirect:/clubs/" + id;
            }
            
            // Join the club
            clubMemberService.joinClub(club, user);
            redirectAttributes.addFlashAttribute("success", "Successfully joined the club!");
            return "redirect:/clubs/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error joining club: " + e.getMessage());
            return "redirect:/clubs/" + id;
        }
    }

    // Edit club form (Admin and Club Head only)
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }
        
        Club club = clubOpt.get();
        
        // Check permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();
                boolean canEdit = currentUser.getRole().name().equals("ADMIN") || 
                                 (club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()));
                
                if (!canEdit) {
                    redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this club");
                    return "redirect:/clubs/" + id;
                }
            }
        }
        
        model.addAttribute("club", club);
        model.addAttribute("title", "Edit " + club.getClubName());
        return "clubs/edit";
    }

    // Handle club edit form submission
    @PostMapping("/{id}/edit")
    public String updateClub(@PathVariable Long id, @Valid @ModelAttribute Club club, 
                           BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "clubs/edit";
        }
        
        try {
            Optional<Club> existingClubOpt = clubService.getClubById(id);
            if (existingClubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Club not found");
                return "redirect:/clubs";
            }
            
            Club existingClub = existingClubOpt.get();
            
            // Check permissions
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                Optional<User> userOpt = userService.getUserByEmail(auth.getName());
                if (userOpt.isPresent()) {
                    User currentUser = userOpt.get();
                    boolean canEdit = currentUser.getRole().name().equals("ADMIN") || 
                                     (existingClub.getHead() != null && existingClub.getHead().getUserId().equals(currentUser.getUserId()));
                    
                    if (!canEdit) {
                        redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this club");
                        return "redirect:/clubs/" + id;
                    }
                }
            }
            
            // Update club details
            existingClub.setClubName(club.getClubName());
            existingClub.setDescription(club.getDescription());
            existingClub.setContactEmail(club.getContactEmail());
            existingClub.setLogoUrl(club.getLogoUrl());
            
            clubService.saveClub(existingClub);
            redirectAttributes.addFlashAttribute("success", "Club updated successfully!");
            return "redirect:/clubs/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating club: " + e.getMessage());
            return "redirect:/clubs/" + id + "/edit";
        }
    }

    // Club analytics page (Admin and Club Head only)
    @GetMapping("/{id}/analytics")
    public String clubAnalytics(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }
        
        Club club = clubOpt.get();
        
        // Check permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();
                boolean canView = currentUser.getRole().name().equals("ADMIN") || 
                                 (club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()));
                
                if (!canView) {
                    redirectAttributes.addFlashAttribute("error", "You don't have permission to view analytics for this club");
                    return "redirect:/clubs/" + id;
                }
            }
        }
        
        // Add analytics data
        model.addAttribute("club", club);
        model.addAttribute("totalMembers", clubMemberService.getActiveMemberCount(club));
        model.addAttribute("totalEvents", eventService.getEventsByClub(club.getClubId()).size());
        model.addAttribute("title", club.getClubName() + " - Analytics");
        
        return "clubs/analytics";
    }

    // Add members form (Club Head only)
    @GetMapping("/{id}/members/add")
    public String showAddMembersForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }
        
        Club club = clubOpt.get();
        
        // Check permissions - only club head can add members
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();
                boolean canAdd = currentUser.getRole().name().equals("ADMIN") || 
                                (club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()));
                
                if (!canAdd) {
                    redirectAttributes.addFlashAttribute("error", "You don't have permission to add members to this club");
                    return "redirect:/clubs/" + id;
                }
            }
        }
        
        model.addAttribute("club", club);
        model.addAttribute("title", "Add Members to " + club.getClubName());
        return "clubs/add-members";
    }

    // Admin panel (Admin and Club Head only)
    @GetMapping("/{id}/admin")
    public String adminPanel(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Club> clubOpt = clubService.getClubById(id);
        
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Club not found");
            return "redirect:/clubs";
        }
        
        Club club = clubOpt.get();
        
        // Check permissions
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();
                boolean canAccess = currentUser.getRole().name().equals("ADMIN") || 
                                   (club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()));
                
                if (!canAccess) {
                    redirectAttributes.addFlashAttribute("error", "You don't have permission to access admin panel for this club");
                    return "redirect:/clubs/" + id;
                }
                
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("isAdmin", currentUser.getRole().name().equals("ADMIN"));
                model.addAttribute("isClubHead", club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()));
            }
        }
        
        // Add admin panel data
        model.addAttribute("club", club);
        model.addAttribute("members", clubMemberService.getActiveMembersByClubWithUser(club.getClubId()));
        model.addAttribute("events", eventService.getEventsByClub(club.getClubId()));
        model.addAttribute("title", club.getClubName() + " - Admin Panel");
        
        return "clubs/admin";
    }
}
