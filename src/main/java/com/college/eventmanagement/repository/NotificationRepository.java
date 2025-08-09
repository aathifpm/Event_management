package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Notification;
import com.college.eventmanagement.model.NotificationType;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser(User user);
    
    List<Notification> findByUserOrderByDateSentDesc(User user);
    
    List<Notification> findByUserAndReadStatus(User user, Boolean readStatus);
    
    List<Notification> findByNotificationType(NotificationType notificationType);
    
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.readStatus = false ORDER BY n.dateSent DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.readStatus = false")
    long countUnreadByUserId(@Param("userId") Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId ORDER BY n.dateSent DESC")
    List<Notification> findByUserIdOrderByDateSentDesc(@Param("userId") Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.dateSent >= :startDate AND n.dateSent <= :endDate")
    List<Notification> findByDateSentBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM Notification n WHERE n.notificationType = :type AND n.relatedEntityId = :entityId")
    List<Notification> findByNotificationTypeAndRelatedEntityId(@Param("type") NotificationType type, 
                                                              @Param("entityId") Long entityId);
    
    @Query("DELETE FROM Notification n WHERE n.user.userId = :userId AND n.readStatus = true AND n.dateSent < :beforeDate")
    void deleteReadNotificationsByUserIdAndDateSentBefore(@Param("userId") Long userId, 
                                                         @Param("beforeDate") LocalDateTime beforeDate);
}
