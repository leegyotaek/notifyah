package com.notifyah.notification.repository;

import com.notifyah.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Notification entity.
 * Provides data access methods for notification operations.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by recipient ID with pagination.
     * 
     * @param recipientId the ID of the notification recipient
     * @param pageable pagination information
     * @return page of notifications for the recipient
     */
    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    /**
     * Count unread notifications for a specific recipient.
     * 
     * @param recipientId the ID of the notification recipient
     * @return count of unread notifications
     */
    long countByRecipientIdAndIsReadFalse(Long recipientId);

    /**
     * Find a specific notification by ID and recipient ID.
     * This ensures a user can only access their own notifications.
     * 
     * @param id the notification ID
     * @param recipientId the ID of the notification recipient
     * @return optional containing the notification if found
     */
    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    /**
     * Mark all unread notifications as read for a specific user.
     * 
     * @param userId the ID of the user
     * @return number of notifications updated
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
} 