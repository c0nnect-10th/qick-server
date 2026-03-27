package connect.qick.domain.notification.dto.response;

import connect.qick.domain.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime receivedAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .receivedAt(notification.getCreatedAt())
                .build();
    }
}
