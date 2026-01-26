package connect.qick.domain.notification.repository;

import connect.qick.domain.notification.entity.Notification;
import connect.qick.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserOrderByCreatedAtDesc(UserEntity user);
}