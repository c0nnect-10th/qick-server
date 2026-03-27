package connect.qick.domain.notification.entity;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification")
public class Notification extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 선생님

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 512)
    private String body;

    @Builder
    public Notification(UserEntity user, String title, String body) {
        this.user = user;
        this.title = title;
        this.body = body;
    }
}
