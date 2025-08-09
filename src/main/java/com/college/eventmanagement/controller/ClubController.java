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
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ClubMemberService clubMemberService;

    // Display all clubs
    @GetMapping
    public String listClubs(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Club> clubs;
        
        if (search != null && !search.trim().isEmpty()) {
            clubs = clubService.searchClubs(search);
            model.addAttribute("searchQuery", search);
        } else {
            clubs = clubService.getActiveClubs();
        }
        
        model.addAttribute("clubs", clubs);
        model.addAttribute("totalClubs", clubService.getActiveClubCount());
        model.addAttribute("title", "Clubs");
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
        List<Event> clubEvents = eventService.getEventsByClub(club.getClubId());
        
        model.addAttribute("club", club);
        model.addAttribute("events", clubEvents);
        model.addAttribute("memberCount", club.getMembers() != null ? club.getMembers().size() : 0);
        model.addAttribute("eventCount", clubEvents.size());
        model.addAttribute("title", club.getClubName());
        
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
        model.addAttribute("club", club);
        model.addAttribute("members", club.getMembers());
        model.addAttribute("title", club.getClubName() + " - Members");
        
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
}
