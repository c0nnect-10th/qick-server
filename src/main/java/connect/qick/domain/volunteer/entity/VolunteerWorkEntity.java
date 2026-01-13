package connect.qick.domain.volunteer.entity;


import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
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
    private int maxParticipants;

    @Column(nullable = false)
    private int currentParticipants;

    @Column(name="difficulty", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkDifficulty difficulty;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column
    private int points; // 굳이 필요할까? 난이도에 따라 포인트가 적용되는거라

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private WorkStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private UserEntity teacher;

    @OneToMany(mappedBy = "volunteerWork", cascade = CascadeType.ALL)
    @Builder.Default
    private List<VolunteerApplicationEntity> applications = new ArrayList<>();

    public void addApplication(VolunteerApplicationEntity application) {
        applications.add(application);
        application.setVolunteerWork(this);
    }

    public void setTeacher(UserEntity teacher) {
        this.teacher = teacher;
        teacher.getVolunteerWorks().add(this);
    }

    //==생성 메서드==//
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
