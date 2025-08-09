package com.college.eventmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations")
public class EventRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reg_id")
    private Long regId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RegistrationStatus status = RegistrationStatus.REGISTERED;
    
    @Column(name = "notes")
    private String notes;
    
    // Constructors
    public EventRegistration() {
        this.registrationDate = LocalDateTime.now();
    }
    
    public EventRegistration(Event event, User user) {
        this();
        this.event = event;
        this.user = user;
    }
    
    public EventRegistration(Event event, User user, String notes) {
        this();
        this.event = event;
        this.user = user;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Long getRegId() {
        return regId;
    }
    
    public void setRegId(Long regId) {
        this.regId = regId;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public RegistrationStatus getStatus() {
        return status;
    }
    
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "EventRegistration{" +
                "regId=" + regId +
                ", event=" + (event != null ? event.getEventName() : "null") +
                ", user=" + (user != null ? user.getName() : "null") +
                ", registrationDate=" + registrationDate +
                ", status=" + status +
                '}';
    }
}
