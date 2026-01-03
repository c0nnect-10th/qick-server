package connect.qick.domain.volunteer.entity;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_application")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VolunteerApplicationEntity extends Base {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private VolunteerWorkEntity volunteerWork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Lob
    @Column
    private String cancelReason;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private Boolean isAttended;

    @PrePersist
    public void prePersist() {
        if (appliedAt == null) {
            appliedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ApplicationStatus.APPLIED;
        }
    }
}