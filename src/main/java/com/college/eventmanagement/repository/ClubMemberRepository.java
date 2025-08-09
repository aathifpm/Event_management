package com.college.eventmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.ClubMember;
import com.college.eventmanagement.model.MemberRole;
import com.college.eventmanagement.model.User;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    
    List<ClubMember> findByClub(Club club);
    
    List<ClubMember> findByUser(User user);
    
    Optional<ClubMember> findByClubAndUser(Club club, User user);
    
    boolean existsByClubAndUser(Club club, User user);
    
    List<ClubMember> findByClubAndIsActiveTrue(Club club);
    
    List<ClubMember> findByUserAndIsActiveTrue(User user);
    
    @Query("SELECT cm FROM ClubMember cm WHERE cm.club.clubId = :clubId AND cm.isActive = true")
    List<ClubMember> findActiveByClubId(@Param("clubId") Long clubId);
    
    @Query("SELECT cm FROM ClubMember cm WHERE cm.user.userId = :userId AND cm.isActive = true")
    List<ClubMember> findActiveByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(cm) FROM ClubMember cm WHERE cm.club.clubId = :clubId AND cm.isActive = true")
    long countActiveByClubId(@Param("clubId") Long clubId);
    
    @Query("SELECT cm FROM ClubMember cm WHERE cm.club.clubId = :clubId AND cm.memberRole = :role AND cm.isActive = true")
    List<ClubMember> findByClubIdAndMemberRoleAndIsActiveTrue(@Param("clubId") Long clubId, @Param("role") MemberRole role);
    
    @Query("SELECT cm FROM ClubMember cm JOIN FETCH cm.user WHERE cm.club.clubId = :clubId AND cm.isActive = true")
    List<ClubMember> findActiveByClubIdWithUser(@Param("clubId") Long clubId);
}
