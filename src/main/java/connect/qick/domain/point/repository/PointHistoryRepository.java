package connect.qick.domain.point.repository;

import connect.qick.domain.point.entity.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {

    // 특정 학생의 포인트 내역 조회 (최신순)
    @Query("""
        SELECT p FROM PointHistoryEntity p
        LEFT JOIN FETCH p.volunteerWork
        WHERE p.student.id = :studentId
        ORDER BY p.createdAt DESC
    """)
    List<PointHistoryEntity> findAllByStudentId(@Param("studentId") Long studentId);

    // 특정 봉사활동의 포인트 내역 조회
    List<PointHistoryEntity> findByVolunteerWorkId(Long workId);
}