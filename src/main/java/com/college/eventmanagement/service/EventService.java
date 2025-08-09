package com.college.eventmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.model.EventRegistration;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.EventRegistrationRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findByEventStatus(EventStatus.UPCOMING);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateAfterAndEventStatusOrderByEventDateAsc(
            LocalDateTime.now(), EventStatus.UPCOMING);
    }

    public List<Event> getEventsByClub(Long clubId) {
        return eventRepository.findByClub_ClubIdOrderByEventDateDesc(clubId);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
        if (event.getEventStatus() == null) {
            event.setEventStatus(EventStatus.UPCOMING);
        }
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public void cancelEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setEventStatus(EventStatus.CANCELLED);
            eventRepository.save(event);
        }
    }

    public long getTotalEventCount() {
        return eventRepository.count();
    }

    public long getActiveEventCount() {
        return eventRepository.countByEventStatus(EventStatus.UPCOMING);
    }

    public long getUpcomingEventCount() {
        return eventRepository.countByEventDateAfterAndEventStatus(LocalDateTime.now(), EventStatus.UPCOMING);
    }

    public List<Event> searchEvents(String keyword) {
        return eventRepository.findByEventNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            keyword, keyword);
    }

    // Role-specific methods for dashboard
    public long getPendingEventCount() {
        return eventRepository.countByEventStatus(EventStatus.UPCOMING);
    }

    public List<Event> getEventsByClubHead(User clubHead) {
        // Get events from clubs where user is head
        return eventRepository.findAll().stream()
            .filter(event -> event.getClub() != null && clubHead.equals(event.getClub().getHead()))
            .toList();
    }

    public List<EventRegistration> getRegistrationsByUser(User user) {
        return eventRegistrationRepository.findByUser(user);
    }

    public List<Event> getRecommendedEvents(User user) {
        // Simple recommendation: upcoming events
        return eventRepository.findByEventDateAfterAndEventStatusOrderByEventDateAsc(
            LocalDateTime.now(), EventStatus.UPCOMING);
    }

    // Registration-related methods
    public boolean isUserRegistered(User user, Event event) {
        return eventRegistrationRepository.existsByEventAndUser(event, user);
    }

    public EventRegistration registerUserForEvent(User user, Event event) throws Exception {
        // Check if user is already registered
        if (isUserRegistered(user, event)) {
            throw new Exception("User is already registered for this event");
        }

        // Check if event has space available
        if (event.getMaxParticipants() != null) {
            long currentRegistrations = eventRegistrationRepository.countRegisteredByEventId(event.getEventId());
            if (currentRegistrations >= event.getMaxParticipants()) {
                throw new Exception("Event is full. No more registrations allowed.");
            }
        }

        // Check if event is still upcoming
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new Exception("Cannot register for past events");
        }

        // Create registration
        EventRegistration registration = new EventRegistration(event, user);
        return eventRegistrationRepository.save(registration);
    }

    public void unregisterUserFromEvent(User user, Event event) throws Exception {
        Optional<EventRegistration> registrationOpt = eventRegistrationRepository.findByEventAndUser(event, user);
        if (registrationOpt.isEmpty()) {
            throw new Exception("User is not registered for this event");
        }

        EventRegistration registration = registrationOpt.get();
        
        // Check if event hasn't started yet
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) { // Allow unregistration up to 2 hours before event
            throw new Exception("Cannot unregister within 2 hours of event start time");
        }

        eventRegistrationRepository.delete(registration);
    }

    public List<EventRegistration> getRegistrationsByEvent(Event event) {
        return eventRegistrationRepository.findByEventIdWithUsers(event.getEventId());
    }

    public long getRegisteredCount(Event event) {
        return eventRegistrationRepository.countRegisteredByEventId(event.getEventId());
    }

    public int getRemainingSpots(Event event) {
        if (event.getMaxParticipants() == null) {
            return -1; // Unlimited
        }
        long registered = getRegisteredCount(event);
        return Math.max(0, event.getMaxParticipants() - (int)registered);
    }
}
