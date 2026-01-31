package connect.qick.domain.notification.service;

import connect.qick.domain.notification.dto.response.NotificationResponse;
import connect.qick.domain.notification.entity.Notification;
import connect.qick.domain.notification.repository.NotificationRepository;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.global.util.PushAlarmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PushAlarmUtil pushAlarmUtil;

    @Transactional
    public void createAndSendNotification(UserEntity teacher, String title, String body) {

        // teacher null 확인
        if (teacher == null) {
            return;
        }

        Notification notification = Notification.builder()
                .user(teacher)
                .title(title)
                .body(body)
                .build();
        notificationRepository.save(notification);

        if (teacher.getFcmToken() != null && !teacher.getFcmToken().isEmpty()) {
            pushAlarmUtil.send(teacher.getFcmToken(), title, body);
        }
    }

    public List<NotificationResponse> getNotificationsForUser(UserEntity user) {
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }
}
