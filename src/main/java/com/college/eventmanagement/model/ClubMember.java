package com.college.eventmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "club_members")
public class ClubMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", referencedColumnName = "club_id", nullable = false)
    private Club club;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
    
    @Column(name = "join_date")
    private LocalDateTime joinDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole = MemberRole.MEMBER;
    
    // Constructors
    public ClubMember() {
        this.joinDate = LocalDateTime.now();
    }
    
    public ClubMember(Club club, User user) {
        this();
        this.club = club;
        this.user = user;
    }
    
    public ClubMember(Club club, User user, MemberRole memberRole) {
        this();
        this.club = club;
        this.user = user;
        this.memberRole = memberRole;
    }
    
    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    
    public Club getClub() {
        return club;
    }
    
    public void setClub(Club club) {
        this.club = club;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public MemberRole getMemberRole() {
        return memberRole;
    }
    
    public void setMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }
    
    @Override
    public String toString() {
        return "ClubMember{" +
                "memberId=" + memberId +
                ", club=" + (club != null ? club.getClubName() : "null") +
                ", user=" + (user != null ? user.getName() : "null") +
                ", joinDate=" + joinDate +
                ", memberRole=" + memberRole +
                ", isActive=" + isActive +
                '}';
    }
}
