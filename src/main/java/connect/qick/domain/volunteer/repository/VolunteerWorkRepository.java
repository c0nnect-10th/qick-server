package connect.qick.domain.volunteer.repository;

import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    SELECT w
    FROM VolunteerWorkEntity w
    ORDER BY
    (
        CASE WHEN
        EXISTS(
            SELECT 1
            FROM VolunteerApplicationEntity a
            WHERE a.volunteerWork = w and
                a.status = 'APPLIED'
        )
        THEN 1 ELSE 0 END
    )
    """)
    List<VolunteerWorkEntity> findAllSummaryOrderByApplications(String googleId);

    //모집 중인 봉사활동 목록 조회
    @Query("""
    select new connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse(
        e.id,
        e.workName,
        e.difficulty,
        e.location,
        t.name,
        e.maxParticipants,
        e.currentParticipants

        )
    from VolunteerWorkEntity e
    join e.teacher t
    where e.status = 'RECRUITING'
    order by e.createdAt desc
    """)
    List<VolunteerWorkSummaryResponse> findAllSummary();

    //삭제되지 않은 봉사활동 조회
    @Query("""
    SELECT e
    FROM VolunteerWorkEntity e
    WHERE e.id = :workId and e.status != 'CANCELLED'
    """)
    Optional<VolunteerWorkEntity> findByWorkId(@Param("workId")Long workId);

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
