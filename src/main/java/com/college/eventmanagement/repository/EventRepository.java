package com.college.eventmanagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByClub(Club club);
    
    List<Event> findByIsActiveTrue();
    
    List<Event> findByEventStatus(EventStatus eventStatus);
    
    @Query("SELECT e FROM Event e WHERE e.club.clubId = :clubId AND e.isActive = true ORDER BY e.eventDate DESC")
    List<Event> findActiveByClubIdOrderByEventDateDesc(@Param("clubId") Long clubId);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate AND e.eventDate <= :endDate AND e.isActive = true")
    List<Event> findByEventDateBetweenAndIsActiveTrue(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate > :currentDate AND e.isActive = true ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE e.eventName LIKE %:name% AND e.isActive = true")
    List<Event> findByEventNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    @Query("SELECT e FROM Event e WHERE e.remainingSpots > 0 AND e.eventDate > :currentDate AND e.isActive = true")
    List<Event> findAvailableEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.club.clubId = :clubId AND e.isActive = true")
    long countActiveByClubId(@Param("clubId") Long clubId);
    
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.registrations WHERE e.eventId = :eventId")
    Event findByIdWithRegistrations(@Param("eventId") Long eventId);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate <= :currentDate AND e.eventDate > :currentDate AND e.eventStatus = 'UPCOMING'")
    List<Event> findEventsWithExpiredRegistration(@Param("currentDate") LocalDateTime currentDate);
    
    // Additional methods for EventService
    List<Event> findByEventDateAfterAndEventStatusOrderByEventDateAsc(LocalDateTime date, EventStatus status);
    
    List<Event> findByClub_ClubIdOrderByEventDateDesc(Long clubId);
    
    long countByEventStatus(EventStatus status);
    
    long countByEventDateAfterAndEventStatus(LocalDateTime date, EventStatus status);
    
    List<Event> findByEventNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
