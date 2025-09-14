package com.example.user_management_service.service.v2;
import com.example.user_management_service.model.v2.Notification;
import com.example.user_management_service.repository.UserRepository;
import com.example.user_management_service.repository.v2.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
/**
 * Date-9/14/2025
 * By Sardor Tokhirov
 * Time-8:33 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Notification createNotification(UUID userId, String message) {
        Notification notification = new Notification();
        notification.setUser(userRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException(String.format("User with id %s not found",userId))));
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserUserId(userId);
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserUserIdAndIsReadFalse(userId);
    }

    public long countUnreadNotifications(UUID userId) {
        return notificationRepository.countByUserUserIdAndIsReadFalse(userId);
    }

    public Notification markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserUserIdAndIsReadFalse(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}