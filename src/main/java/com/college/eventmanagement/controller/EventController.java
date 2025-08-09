package com.college.eventmanagement.controller;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ClubService clubService;

    @GetMapping("/events")
    public String events(Model model, @RequestParam(value = "search", required = false) String search) {
        List<Event> events;
        
        if (search != null && !search.trim().isEmpty()) {
            events = eventService.searchEvents(search.trim());
            model.addAttribute("searchTerm", search);
        } else {
            events = eventService.getUpcomingEvents();
        }
        
        model.addAttribute("events", events);
        model.addAttribute("clubs", clubService.getActiveClubs());
        
        return "events/list";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
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
}
