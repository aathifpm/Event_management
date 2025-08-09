package com.college.eventmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

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
}
