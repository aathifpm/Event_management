package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventRegistration;
import com.college.eventmanagement.model.RegistrationStatus;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    
    List<EventRegistration> findByEvent(Event event);
    
    List<EventRegistration> findByUser(User user);
    
    Optional<EventRegistration> findByEventAndUser(Event event, User user);
    
    boolean existsByEventAndUser(Event event, User user);
    
    List<EventRegistration> findByStatus(RegistrationStatus status);
    
    @Query("SELECT er FROM EventRegistration er WHERE er.event.eventId = :eventId AND er.status = :status")
    List<EventRegistration> findByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RegistrationStatus status);
    
    @Query("SELECT er FROM EventRegistration er WHERE er.user.userId = :userId AND er.status = :status")
    List<EventRegistration> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") RegistrationStatus status);
    
    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event.eventId = :eventId AND er.status = 'REGISTERED'")
    long countRegisteredByEventId(@Param("eventId") Long eventId);
    
    @Query("SELECT er FROM EventRegistration er JOIN FETCH er.event WHERE er.user.userId = :userId ORDER BY er.registrationDate DESC")
    List<EventRegistration> findByUserIdWithEvents(@Param("userId") Long userId);
    
    @Query("SELECT er FROM EventRegistration er JOIN FETCH er.user WHERE er.event.eventId = :eventId")
    List<EventRegistration> findByEventIdWithUsers(@Param("eventId") Long eventId);
    
    @Query("SELECT er FROM EventRegistration er WHERE er.event.eventDate BETWEEN :startDate AND :endDate AND er.status = 'REGISTERED'")
    List<EventRegistration> findRegisteredBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.user.userId = :userId AND er.status = 'ATTENDED'")
    long countAttendedEventsByUserId(@Param("userId") Long userId);
}
