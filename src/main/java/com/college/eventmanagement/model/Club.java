package com.college.eventmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clubs")
public class Club {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long clubId;
    
    @NotBlank(message = "Club name is required")
    @Size(min = 2, max = 100, message = "Club name must be between 2 and 100 characters")
    @Column(name = "club_name", nullable = false, unique = true)
    private String clubName;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", referencedColumnName = "user_id")
    private User head;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    // Relationships
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<ClubMember> members;
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Event> events;
    
    // Constructors
    public Club() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Club(String clubName, String description, User head) {
        this();
        this.clubName = clubName;
        this.description = description;
        this.head = head;
    }
    
    // Getters and Setters
    public Long getClubId() {
        return clubId;
    }
    
    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }
    
    public String getClubName() {
        return clubName;
    }
    
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public User getHead() {
        return head;
    }
    
    public void setHead(User head) {
        this.head = head;
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
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public List<ClubMember> getMembers() {
        return members;
    }
    
    public void setMembers(List<ClubMember> members) {
        this.members = members;
    }
    
    public List<Event> getEvents() {
        return events;
    }
    
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    @Override
    public String toString() {
        return "Club{" +
                "clubId=" + clubId +
                ", clubName='" + clubName + '\'' +
                ", description='" + description + '\'' +
                ", head=" + (head != null ? head.getName() : "null") +
                ", isActive=" + isActive +
                '}';
    }
}
