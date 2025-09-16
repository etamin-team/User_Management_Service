package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.dto.NotificationCreateDto;
import com.example.user_management_service.model.dto.NotificationDto;
import com.example.user_management_service.model.v2.Notification;
import com.example.user_management_service.service.v2.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
/**
 * Date-9/14/2025
 * By Sardor Tokhirov
 * Time-8:32 PM (GMT+5)
 */
@RequestMapping("/api/v2/notifications")
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationCreateDto request) {
        Notification notification = notificationService.createNotification(
                request.getUserId(), request.getMessage());
        return ResponseEntity.ok(new NotificationDto(
                notification.getNotificationId(),
                notification.getUser().getUserId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedDate()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable UUID userId) {
        List<NotificationDto> dtos = notificationService.getUserNotifications(userId).stream()
                .map(n -> new NotificationDto(
                        n.getNotificationId(),
                        n.getUser().getUserId(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedDate()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(@PathVariable UUID userId) {
        List<NotificationDto> dtos = notificationService.getUnreadNotifications(userId).stream()
                .map(n -> new NotificationDto(
                        n.getNotificationId(),
                        n.getUser().getUserId(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedDate()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(userId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable UUID notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(new NotificationDto(
                notification.getNotificationId(),
                notification.getUser().getUserId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedDate()));
    }

    @PutMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
