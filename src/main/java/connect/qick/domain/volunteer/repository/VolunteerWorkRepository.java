package connect.qick.domain.volunteer.repository;

import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VolunteerWorkRepository extends JpaRepository<VolunteerWorkEntity, Long> {

    @Deprecated
    @Query("""
    SELECT w
    FROM VolunteerWorkEntity w
    ORDER BY
    (
        CASE WHEN
        EXISTS(
            SELECT 1
            FROM VolunteerApplicationEntity a
            WHERE a.volunteerWork = w and
                a.status = 'APPLIED' and
                a.student.googleId = :googleId
        )
        THEN 1 ELSE 0 END
    ) desc,
    w.createdAt desc
    """)
    List<VolunteerWorkEntity> findAllOrderByApplications(String googleId);

    //모집 중인 봉사활동 목록 조회
    @Query("""
    SELECT new connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse(
        w.id,
        w.workName,
        w.difficulty,
        w.location,
        t.name,
        w.maxParticipants,
        w.currentParticipants,
        CASE WHEN a.id IS NOT NULL THEN true ELSE false END)
    FROM VolunteerWorkEntity w
    JOIN w.teacher t
    LEFT JOIN VolunteerApplicationEntity a
        ON a.volunteerWork = w
        AND a.student.googleId =:googleId
        AND a.status  = connect.qick.domain.volunteer.enums.ApplicationStatus.APPLIED
    WHERE w.status IN (connect.qick.domain.volunteer.enums.WorkStatus.RECRUITING, connect.qick.domain.volunteer.enums.WorkStatus.ONGOING)
    ORDER BY
        (CASE
            WHEN a.id IS NOT NULL AND w.status = 'ONGOING' THEN 4
            WHEN a.id IS NOT NULL AND w.status = 'RECRUITING' THEN 3
            WHEN a.id IS NULL AND w.status = 'RECRUITING' THEN 2
            WHEN a.id IS NULL AND w.status = 'ONGOING' THEN 1
        END) DESC,
        w.createdAt desc
    """)
    List<VolunteerWorkSummaryResponse> findAllSummary(String googleId);



    // 스케줄러용 모집중인 봉사활동 조회 하는거
    List<VolunteerWorkEntity> findByStatusAndStartTimeBefore(
            WorkStatus status,
            LocalDateTime startTime
    );

    // 선생님이 생성한 봉사활동 목록 조회 하는데 상태별로 필터링 하는거
    List<VolunteerWorkEntity> findByTeacherIdAndStatus(Long teacherId, WorkStatus status);

    // 선생님이 생성한 모든 봉사활동 조회
    @Query("""
        SELECT w FROM VolunteerWorkEntity w
        WHERE w.teacher.id = :teacherId
        ORDER BY w.createdAt DESC
    """)
    List<VolunteerWorkEntity> findAllByTeacherId(@Param("teacherId") Long teacherId);

    @Query("""
        SELECT w
        FROM VolunteerWorkEntity w
        WHERE w.teacher.id = :teacherId
        AND w.status in (connect.qick.domain.volunteer.enums.WorkStatus.ONGOING, connect.qick.domain.volunteer.enums.WorkStatus.RECRUITING)
        ORDER BY
        CASE WHEN w.status = 'ONGOING' THEN 1 WHEN w.status = 'RECRUITING' THEN 0 END,
        w.createdAt DESC
    """)
    List<VolunteerWorkEntity> findAllOrderByStatus(Long teacherId);

    List<VolunteerWorkEntity> findByStatusAndStartTimeBetween(WorkStatus status, LocalDateTime start, LocalDateTime end);

}
