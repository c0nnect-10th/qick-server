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

    @Query("""
    SELECT w
    FROM VolunteerWorkEntity w
    LEFT OUTER JOIN w.applications a
    WHERE w.status = 'RECRUITING'
    ORDER BY
        CASE
            WHEN a.student.id = :userId THEN 0
            ELSE 1
        END,
        w.createdAt desc
    """)
    List<VolunteerWorkEntity> findAllSummaryByUserId(Long userId);

    @Query("""
    SELECT new connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse(
        e.id,
        e.workName,
        e.difficulty,
        e.location,
        t.name,
        e.maxParticipants,
        e.currentParticipants,
        (CASE WHEN a.id = :googleId THEN true ELSE false END)
    )
    FROM VolunteerWorkEntity e
    JOIN e.teacher t
    LEFT JOIN e.applications a
    WHERE e.status = 'RECRUITING'
    ORDER BY
        CASE\s
            WHEN a.id = :googleId\s
            THEN 1\s
            ELSE 0
        END DESC,
        e.createdAt DESC
    """)
    List<VolunteerWorkSummaryResponse> findAllSummaryByGoogleId(String googleId);

    @Query("""
    select new connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse(
        e.id,
        e.workName,
        e.difficulty,
        e.location,
        t.name,
        e.maxParticipants,
        e.currentParticipants,
        false
        )
    from VolunteerWorkEntity e
    join e.teacher t
    where e.status = 'RECRUITING'
    order by e.createdAt desc
    """)
    List<VolunteerWorkSummaryResponse> findAllSummary();

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

}
