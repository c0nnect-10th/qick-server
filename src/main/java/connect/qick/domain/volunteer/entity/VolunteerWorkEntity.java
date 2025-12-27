package connect.qick.domain.volunteer.entity;


import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime start_time;

//    @Column end_time는 딱히 필요가 없어서 지웠습니다
//    private LocalDateTime end_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private UserEntity teacher; // 어케 불러올 것인지 찾아봐야할듯

}
