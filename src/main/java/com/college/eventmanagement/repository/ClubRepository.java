package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Club;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    
    Optional<Club> findByClubName(String clubName);
    
    boolean existsByClubName(String clubName);
    
    List<Club> findByIsActiveTrue();
    
    List<Club> findByHead(User head);
    
    List<Club> findByHeadAndIsActiveTrue(User head);
    
    @Query("SELECT c FROM Club c WHERE c.head.userId = :headId AND c.isActive = true")
    List<Club> findByHeadIdAndIsActiveTrue(@Param("headId") Long headId);
    
    @Query("SELECT c FROM Club c WHERE c.clubName LIKE %:name% AND c.isActive = true")
    List<Club> findByClubNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    @Query("SELECT COUNT(c) FROM Club c WHERE c.isActive = true")
    long countActiveClubs();
    
    long countByIsActiveTrue();
    
    @Query("SELECT c FROM Club c LEFT JOIN FETCH c.members WHERE c.isActive = true")
    List<Club> findAllActiveClubsWithMembers();
    
    @Query("SELECT c FROM Club c LEFT JOIN FETCH c.events WHERE c.clubId = :clubId")
    Optional<Club> findByIdWithEvents(@Param("clubId") Long clubId);
}
