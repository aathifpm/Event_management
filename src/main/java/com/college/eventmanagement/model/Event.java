package com.college.eventmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", referencedColumnName = "club_id", nullable = false)
    private Club club;
    
    @NotBlank(message = "Event name is required")
    @Size(min = 2, max = 100, message = "Event name must be between 2 and 100 characters")
    @Column(name = "event_name", nullable = false)
    private String eventName;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @Future(message = "Event date must be in the future")
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Venue is required")
    @Column(name = "venue", nullable = false)
    private String venue;
    
    @Min(value = 1, message = "Maximum participants must be at least 1")
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "remaining_spots")
    private Integer remainingSpots;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "event_image_url")
    private String eventImageUrl;
    
    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_status")
    private EventStatus eventStatus = EventStatus.UPCOMING;
    
    // Relationships
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventRegistration> registrations;
    
    // Constructors
    public Event() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Event(Club club, String eventName, String description, LocalDateTime eventDate, 
                 String venue, Integer maxParticipants) {
        this();
        this.club = club;
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
        this.maxParticipants = maxParticipants;
        this.remainingSpots = maxParticipants;
    }
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public Club getClub() {
        return club;
    }
    
    public void setClub(Club club) {
        this.club = club;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getEventDate() {
        return eventDate;
    }
    
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
    
    public String getVenue() {
        return venue;
    }
    
    public void setVenue(String venue) {
        this.venue = venue;
    }
    
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
        this.remainingSpots = maxParticipants;
    }
    
    public Integer getRemainingSpots() {
        return remainingSpots;
    }
    
    public void setRemainingSpots(Integer remainingSpots) {
        this.remainingSpots = remainingSpots;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getEventImageUrl() {
        return eventImageUrl;
    }
    
    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }
    
    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }
    
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
    
    public EventStatus getEventStatus() {
        return eventStatus;
    }
    
    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
    
    public List<EventRegistration> getRegistrations() {
        return registrations;
    }
    
    public void setRegistrations(List<EventRegistration> registrations) {
        this.registrations = registrations;
    }
    
    // Helper methods
    public void decrementRemainingSpots() {
        if (this.remainingSpots > 0) {
            this.remainingSpots--;
        }
    }
    
    public void incrementRemainingSpots() {
        if (this.remainingSpots < this.maxParticipants) {
            this.remainingSpots++;
        }
    }
    
    public boolean hasAvailableSpots() {
        return this.remainingSpots > 0;
    }
    
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", club=" + (club != null ? club.getClubName() : "null") +
                ", eventName='" + eventName + '\'' +
                ", eventDate=" + eventDate +
                ", venue='" + venue + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", remainingSpots=" + remainingSpots +
                ", eventStatus=" + eventStatus +
                ", isActive=" + isActive +
                '}';
    }
}
