package connect.qick.domain.point.entity;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.point.enums.PointType;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_history")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointHistoryEntity extends Base {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")
    private VolunteerWorkEntity volunteerWork;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType pointType;

    @Column(nullable = false)
    private Integer points;

    @Lob
    @Column
    private String reason;
}