package connect.qick.domain.volunteer.entity;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
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
    @JoinColumn(name = "work_id")
    private VolunteerWorkEntity volunteerWork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
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

    //==연관관계 편의 메서드==//
    public void setStudent(UserEntity student) {
        this.student = student;
        student.getVolunteerApplications().add(this);
    }

    //==비즈니스 로직==//
    public void validateStudent(String googleId) {
        this.student.checkGoogleId(googleId);
    }

    /**
     * 봉사활동 완료
     */
    public void complete() {
        status = ApplicationStatus.COMPLETED;
        isAttended = true;
        completedAt = LocalDateTime.now();
    }

    /**
     * 봉사활동 미참여
     */
    public void notComplete() {
        status = ApplicationStatus.NO_SHOW;
        isAttended = false;
    }

    public boolean isApplied() {
        return status == ApplicationStatus.APPLIED;
    }

    public void markAttendance(boolean attended) {
        if (status != ApplicationStatus.APPLIED) return;

        if (attended) {
            complete();
        } else {
            notComplete();
        }
    }

    public void cancel(String cancelReason) {
        if (this.status != ApplicationStatus.APPLIED) {
            throw new VolunteerException(VolunteerStatusCode.CANNOT_CANCEL);
        }

        this.status = ApplicationStatus.CANCELLED;
        this.cancelReason = cancelReason;
        this.cancelledAt = LocalDateTime.now();

        this.volunteerWork.cancelApplication();
    }

}