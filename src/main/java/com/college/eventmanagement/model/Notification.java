package com.college.eventmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notif_id")
    private Long notifId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "date_sent")
    private LocalDateTime dateSent;
    
    @Column(name = "read_status")
    private Boolean readStatus = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId;
    
    @Column(name = "title")
    private String title;
    
    // Constructors
    public Notification() {
        this.dateSent = LocalDateTime.now();
    }
    
    public Notification(User user, String title, String message, NotificationType notificationType) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }
    
    public Notification(User user, String title, String message, NotificationType notificationType, Long relatedEntityId) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.relatedEntityId = relatedEntityId;
    }
    
    // Getters and Setters
    public Long getNotifId() {
        return notifId;
    }
    
    public void setNotifId(Long notifId) {
        this.notifId = notifId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getDateSent() {
        return dateSent;
    }
    
    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }
    
    public Boolean getReadStatus() {
        return readStatus;
    }
    
    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    
    public Long getRelatedEntityId() {
        return relatedEntityId;
    }
    
    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "notifId=" + notifId +
                ", user=" + (user != null ? user.getName() : "null") +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", dateSent=" + dateSent +
                ", readStatus=" + readStatus +
                ", notificationType=" + notificationType +
                '}';
    }
}
