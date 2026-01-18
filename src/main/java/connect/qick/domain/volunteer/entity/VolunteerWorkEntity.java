package connect.qick.domain.volunteer.entity;


import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="volunteer_work")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VolunteerWorkEntity extends Base {
    @Column
    private String workName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private Integer currentParticipants;

    @Column(name="difficulty", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkDifficulty difficulty;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column
    private Integer points; // 굳이 필요할까? 난이도에 따라 포인트가 적용되는거라

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private WorkStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private UserEntity teacher;

    @OneToMany(mappedBy = "volunteerWork", cascade = CascadeType.ALL)
    @Builder.Default
    private List<VolunteerApplicationEntity> applications = new ArrayList<>();

    private boolean reminder5Sent;
    private boolean reminder10Sent;

    //==연관관계 편의 메서드==//
    public void addApplication(VolunteerApplicationEntity application) {
        if (maxParticipants <= currentParticipants) {
            throw new VolunteerException(VolunteerStatusCode.RECRUITMENT_FULL);
        }
        currentParticipants++;
        applications.add(application);
        application.setVolunteerWork(this);
    }

    public void cancelApplication() {
        if (status != WorkStatus.RECRUITING) {
            throw new VolunteerException(VolunteerStatusCode.CANNOT_CANCEL);
        }
        currentParticipants--;
    }

    public void setTeacher(UserEntity teacher) {
        this.teacher = teacher;
        teacher.getVolunteerWorks().add(this);
    }

    //==비즈니스 메서드==//
    /**
     * 봉사활동 취소
     * 봉사활동을 만든 사용자가 맞는지 확인 후 봉사활동을 취소합니다.
     */
    public void cancelBy(String googleId) {
        if (status != WorkStatus.RECRUITING) {
            throw new VolunteerException(VolunteerStatusCode.CANNOT_CANCEL);
        }

        this.teacher.checkGoogleId(googleId);
        applications.forEach(application -> application.cancel("봉사활동이 취소되었습니다."));
        status = WorkStatus.CANCELLED;
        teacher.getVolunteerWorks().remove(this);
    }

    public void complete() {
        status = WorkStatus.COMPLETED;
    }

    public void validateTeacher(String googleId) {
        this.teacher.checkGoogleId(googleId);
    }

    public void validateCompletable() {
        if (status != WorkStatus.ONGOING) {
            throw new VolunteerException(VolunteerStatusCode.INVALID_WORK_STATUS);
        }
    }

    public void validateApplication() {
        if (status != WorkStatus.RECRUITING) {
            throw new VolunteerException(VolunteerStatusCode.INVALID_WORK_STATUS);
        }
    }

    //==생성 메서드==//
    /**
     * 봉사활동 생성
     * @param teacher 봉사활동을 생성하는 유저(선생님)
     * @param request requestDTO
     * @return
     */
    public static VolunteerWorkEntity createVolunteerWork(UserEntity teacher, CreateVolunteerWorkRequest request) {
        VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                .workName(request.name())
                .maxParticipants(request.maxParticipants())
                .currentParticipants(0)
                .location(request.location())
                .status(WorkStatus.RECRUITING)
                .description(request.description())
                .difficulty(request.difficulty())
                .startTime(request.startTime())
                .build();

        work.setTeacher(teacher);
        return work;
    }

}
