package connect.qick.domain.volunteer.repository;

import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplicationEntity, Long> {

    // 특정 학생의 특정 봉사활동 신청 내역 조회
    Optional<VolunteerApplicationEntity> findByVolunteerWorkIdAndStudentId(Long workId, Long studentId);

    // 특정 학생의 모든 신청 내역 조회
    @Query("""
        SELECT a FROM VolunteerApplicationEntity a
        JOIN FETCH a.volunteerWork w
        JOIN FETCH w.teacher
        WHERE a.student.id = :studentId
        ORDER BY a.appliedAt DESC
    """)
    List<VolunteerApplicationEntity> findAllByStudentId(@Param("studentId") Long studentId);

    // 특정 학생의 특정 상태의 신청 내역 조회
    List<VolunteerApplicationEntity> findByStudentIdAndStatus(Long studentId, ApplicationStatus status);

    // 특정 봉사활동의 모든 신청 내역 조회
    @Query("""
        SELECT a FROM VolunteerApplicationEntity a
        JOIN FETCH a.student
        WHERE a.volunteerWork.id = :workId
        ORDER BY a.appliedAt ASC
    """)
    List<VolunteerApplicationEntity> findAllByVolunteerWorkId(@Param("workId") Long workId);

    List<VolunteerApplicationEntity> findAllByVolunteerWorkAndStatus(VolunteerWorkEntity volunteerWork, ApplicationStatus status);

    // 특정 봉사활동의 APPLIED 상태인 신청 수 조회
    @Query("""
        SELECT COUNT(a) FROM VolunteerApplicationEntity a
        WHERE a.volunteerWork.id = :workId
        AND a.status = 'APPLIED'
    """)
    long countAppliedByWorkId(@Param("workId") Long workId);

    // 중복 신청 체크
    boolean existsByVolunteerWorkIdAndStudentIdAndStatus(Long workId, Long studentId, ApplicationStatus status);
}