package connect.qick.global.util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushAlarmUtil {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * 단일 사용자에게 푸시 알림 전송
     * @param fcmToken 대상 사용자의 FCM 토큰
     * @param title 알림 제목
     * @param body 알림 내용
     */
    public void send(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.error("FCM token is null or empty, skipping push notification");
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message to token " + fcmToken + ": " + response);
        } catch (Exception e) {
            log.error("Failed to send message to token " + fcmToken + ": " + e.getMessage());
        }
    }

    /**
     * 여러 사용자에게 푸시 알림 전송
     * @param fcmTokens 대상 사용자들의 FCM 토큰 리스트
     * @param title 알림 제목
     * @param body 알림 내용
     */
    public void sendMulticast(List<String> fcmTokens, String title, String body) {
        List<String> validTokens = fcmTokens.stream()
                .filter(token -> token != null && !token.isEmpty())
                .toList();

        if (validTokens.isEmpty()) {
            log.error("No valid FCM tokens provided for multicast, skipping push notification.");
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(validTokens)
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.sendEachForMulticast(multicastMessage);
            log.info("Successfully sent multicast message to " + validTokens.size() + " tokens.");
        } catch (Exception e) {
            log.error("Failed to send multicast message: " + e.getMessage());
        }
    }
}
