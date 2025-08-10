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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventRegistration;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.service.ClubService;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.UserService;

import jakarta.validation.Valid;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ClubService clubService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/events")
    public String events(Model model, @RequestParam(value = "search", required = false) String search) {
        List<Event> events;
        
        // Get current user to filter events based on role
        User currentUser = getCurrentUser();
        
        if (search != null && !search.trim().isEmpty()) {
            events = eventService.searchEvents(search.trim());
            model.addAttribute("searchTerm", search);
        } else {
            // Filter events based on user role
            if (currentUser != null) {
                switch (currentUser.getRole()) {
                    case ADMIN:
                        // Admin sees all events
                        events = eventService.getAllEvents();
                        model.addAttribute("showAdminActions", true);
                        break;
                    case CLUB_HEAD:
                        // Club heads see all events but with special actions for their club's events
                        events = eventService.getAllEvents();
                        model.addAttribute("userClubs", clubService.getClubsByHead(currentUser));
                        model.addAttribute("showClubHeadActions", true);
                        break;
                    case STUDENT:
                    default:
                        // Students see only upcoming active events
                        events = eventService.getUpcomingEvents();
                        break;
                }
            } else {
                // Non-authenticated users see only upcoming events
                events = eventService.getUpcomingEvents();
            }
        }
        
        model.addAttribute("events", events);
        model.addAttribute("clubs", clubService.getActiveClubs());
        model.addAttribute("currentUser", currentUser);
        
        return "events/list";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            User currentUser = getCurrentUser();
            
            model.addAttribute("event", event);
            
            // Add role-based permissions
            if (currentUser != null) {
                boolean canEdit = currentUser.getRole().name().equals("ADMIN") || 
                                (currentUser.getRole().name().equals("CLUB_HEAD") && 
                                 event.getClub() != null && 
                                 clubService.isUserClubHead(currentUser, event.getClub()));
                                 
                model.addAttribute("canEditEvent", canEdit);
                model.addAttribute("canViewRegistrations", canEdit);
                
                // Check if user is already registered
                model.addAttribute("isRegistered", eventService.isUserRegistered(currentUser, event));
                
                // Add registration statistics
                model.addAttribute("registeredCount", eventService.getRegisteredCount(event));
                model.addAttribute("remainingSpots", eventService.getRemainingSpots(event));
            }
            
            return "events/details";
        } else {
            return "redirect:/events";
        }
    }

    @GetMapping("/events/club/{clubId}")
    public String eventsByClub(@PathVariable Long clubId, Model model) {
        Optional<Club> clubOpt = clubService.getClubById(clubId);
        
        if (clubOpt.isPresent()) {
            Club club = clubOpt.get();
            List<Event> events = eventService.getEventsByClub(clubId);
            
            model.addAttribute("club", club);
            model.addAttribute("events", events);
            
            return "events/club-events";
        } else {
            return "redirect:/events";
        }
    }
    
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                String userEmail = authentication.getName();
                return userService.getUserByEmail(userEmail).orElse(null);
            }
        } catch (Exception e) {
            // Handle exception
        }
        return null;
    }

    // Show create event form
    @GetMapping("/events/create")
    public String showCreateForm(Model model, @RequestParam(value = "clubId", required = false) Long clubId) {
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Check if user has permission to create events
        boolean canCreate;
        canCreate = switch (currentUser.getRole()) {
            case ADMIN -> true;
            case CLUB_HEAD -> true;
            default -> false;
        };
        
        if (!canCreate) {
            model.addAttribute("error", "You don't have permission to create events");
            return "redirect:/events";
        }
        
        Event event = new Event();
        
        // If clubId is provided, pre-select the club
        if (clubId != null) {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isPresent()) {
                Club club = clubOpt.get();
                // Check if user can create events for this club
                if (currentUser.getRole().name().equals("ADMIN") || 
                    (club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()))) {
                    event.setClub(club);
                    model.addAttribute("selectedClub", club);
                }
            }
        }
        
        // Get available clubs based on user role
        List<Club> availableClubs;
        if (currentUser.getRole().name().equals("ADMIN")) {
            availableClubs = clubService.getAllClubs();
        } else {
            availableClubs = clubService.getClubsByHead(currentUser);
        }
        
        model.addAttribute("event", event);
        model.addAttribute("clubs", availableClubs);
        model.addAttribute("title", "Create New Event");
        
        return "events/create";
    }

    // Handle event creation
    @PostMapping("/events/create")
    public String createEvent(@Valid @ModelAttribute Event event, 
                            BindingResult result, 
                            Model model, 
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            // Get available clubs again for the form
            List<Club> availableClubs;
            if (currentUser.getRole().name().equals("ADMIN")) {
                availableClubs = clubService.getAllClubs();
            } else {
                availableClubs = clubService.getClubsByHead(currentUser);
            }
            model.addAttribute("clubs", availableClubs);
            model.addAttribute("title", "Create New Event");
            return "events/create";
        }
        
        try {
            // Validate club selection
            if (event.getClub() == null || event.getClub().getClubId() == null) {
                redirectAttributes.addFlashAttribute("error", "Please select a club for the event");
                return "redirect:/events/create";
            }
            
            Optional<Club> clubOpt = clubService.getClubById(event.getClub().getClubId());
            if (clubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Selected club not found");
                return "redirect:/events/create";
            }
            
            Club club = clubOpt.get();
            
            // Check permissions
            boolean canCreate = currentUser.getRole().name().equals("ADMIN") || 
                               (club.getHead() != null && club.getHead().getUserId().equals(currentUser.getUserId()));
            
            if (!canCreate) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to create events for this club");
                return "redirect:/events/create";
            }
            
            // Set the club and save
            event.setClub(club);
            Event savedEvent = eventService.saveEvent(event);
            
            redirectAttributes.addFlashAttribute("success", "Event created successfully!");
            return "redirect:/events/" + savedEvent.getEventId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating event: " + e.getMessage());
            return "redirect:/events/create";
        }
    }

    // Show edit event form
    @GetMapping("/events/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found");
            return "redirect:/events";
        }
        
        Event event = eventOpt.get();
        
        // Check permissions
        boolean canEdit = currentUser.getRole().name().equals("ADMIN") || 
                         (currentUser.getRole().name().equals("CLUB_HEAD") && 
                          event.getClub() != null && 
                          clubService.isUserClubHead(currentUser, event.getClub()));
        
        if (!canEdit) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this event");
            return "redirect:/events";
        }
        
        // Get available clubs based on user role
        List<Club> availableClubs;
        if (currentUser.getRole().name().equals("ADMIN")) {
            availableClubs = clubService.getAllClubs();
        } else {
            availableClubs = clubService.getClubsByHead(currentUser);
        }
        
        model.addAttribute("event", event);
        model.addAttribute("clubs", availableClubs);
        model.addAttribute("title", "Edit Event");
        
        return "events/create"; // Reuse the create template for editing
    }

    // Handle event update
    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable Long id,
                            @Valid @ModelAttribute Event event,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Event> existingEventOpt = eventService.getEventById(id);
        if (existingEventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found");
            return "redirect:/events";
        }
        
        Event existingEvent = existingEventOpt.get();
        
        // Check permissions
        boolean canEdit = currentUser.getRole().name().equals("ADMIN") || 
                         (currentUser.getRole().name().equals("CLUB_HEAD") && 
                          existingEvent.getClub() != null && 
                          clubService.isUserClubHead(currentUser, existingEvent.getClub()));
        
        if (!canEdit) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this event");
            return "redirect:/events";
        }
        
        if (result.hasErrors()) {
            List<Club> availableClubs;
            if (currentUser.getRole().name().equals("ADMIN")) {
                availableClubs = clubService.getAllClubs();
            } else {
                availableClubs = clubService.getClubsByHead(currentUser);
            }
            model.addAttribute("clubs", availableClubs);
            model.addAttribute("title", "Edit Event");
            return "events/create";
        }
        
        try {
            // Update existing event fields
            existingEvent.setEventName(event.getEventName());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setEventDate(event.getEventDate());
            existingEvent.setVenue(event.getVenue());
            existingEvent.setMaxParticipants(event.getMaxParticipants());
            
            // Only allow club change for admins
            if (currentUser.getRole().name().equals("ADMIN") && event.getClub() != null) {
                Optional<Club> clubOpt = clubService.getClubById(event.getClub().getClubId());
                if (clubOpt.isPresent()) {
                    existingEvent.setClub(clubOpt.get());
                }
            }
            
            eventService.saveEvent(existingEvent);
            
            redirectAttributes.addFlashAttribute("success", "Event updated successfully!");
            return "redirect:/events/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating event: " + e.getMessage());
            return "redirect:/events/" + id + "/edit";
        }
    }

    // Delete event
    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found");
            return "redirect:/events";
        }
        
        // Event event = eventOpt.get();
        
        // Check permissions - only admin can delete events
        if (!currentUser.getRole().name().equals("ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this event");
            return "redirect:/events";
        }
        
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("success", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting event: " + e.getMessage());
        }
        
        return "redirect:/events";
    }

    // View event participants
    @GetMapping("/events/{id}/participants")
    public String viewParticipants(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found");
            return "redirect:/events";
        }
        
        Event event = eventOpt.get();
        
        // Check permissions
        boolean canView = currentUser.getRole().name().equals("ADMIN") || 
                         (currentUser.getRole().name().equals("CLUB_HEAD") && 
                          event.getClub() != null && 
                          clubService.isUserClubHead(currentUser, event.getClub()));
        
        if (!canView) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to view participants for this event");
            return "redirect:/events";
        }
        
        // Get event registrations
        List<EventRegistration> registrations = eventService.getRegistrationsByEvent(event);
        model.addAttribute("registrations", registrations);
        model.addAttribute("event", event);
        model.addAttribute("registeredCount", registrations.size());
        
        return "events/participants";
    }

    // Register for event
    @PostMapping("/events/{id}/register")
    public String registerForEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found");
            return "redirect:/events";
        }
        
        Event event = eventOpt.get();
        
        // Only students can register
        if (!currentUser.getRole().name().equals("STUDENT")) {
            redirectAttributes.addFlashAttribute("error", "Only students can register for events");
            return "redirect:/events/" + id;
        }
        
        try {
            // Check if already registered
            boolean isRegistered = eventService.isUserRegistered(currentUser, event);
            if (isRegistered) {
                redirectAttributes.addFlashAttribute("error", "You are already registered for this event");
                return "redirect:/events/" + id;
            }
            
            // Register user for event
            eventService.registerUserForEvent(currentUser, event);
            
            redirectAttributes.addFlashAttribute("success", "Successfully registered for the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error registering for event: " + e.getMessage());
        }
        
        return "redirect:/events/" + id;
    }

    // Unregister from event
    @PostMapping("/events/{id}/unregister")
    public String unregisterFromEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Event not found");
            return "redirect:/events";
        }
        
        Event event = eventOpt.get();
        
        try {
            eventService.unregisterUserFromEvent(currentUser, event);
            redirectAttributes.addFlashAttribute("success", "Successfully unregistered from the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error unregistering from event: " + e.getMessage());
        }
        
        return "redirect:/events/" + id;
    }

    // Register for event (GET - for convenience links)
    @GetMapping("/events/{id}/register")
    public String registerForEventGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return registerForEvent(id, redirectAttributes);
    }

    // Unregister from event (GET - for convenience links)
    @GetMapping("/events/{id}/unregister")
    public String unregisterFromEventGet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return unregisterFromEvent(id, redirectAttributes);
    }
}
