package connect.qick.domain.notification.controller;

import connect.qick.domain.notification.dto.response.NotificationResponse;
import connect.qick.domain.notification.service.NotificationService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.security.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(customUserDetails.getUserEntity());
        return ApiResponse.ok(notifications);
    }
}
