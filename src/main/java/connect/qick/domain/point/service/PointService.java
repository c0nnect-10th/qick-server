package connect.qick.domain.point.service;

import connect.qick.domain.point.dto.response.PointHistoryResponse;
import connect.qick.domain.point.entity.PointHistoryEntity;
import connect.qick.domain.point.enums.PointType;
import connect.qick.domain.point.repository.PointHistoryRepository;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

//  난이도별 포인트 계산
    private int calculatePoints(WorkDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 40;
            case NORMAL -> 60;
            case HARD -> 100;
        };
    }

//  봉사활동 완료 시 포인트 지급
    @Transactional
    public void earnPoints(UserEntity student, VolunteerWorkEntity work) {
        int points = calculatePoints(work.getDifficulty());

        // 포인트 내역 생성
        PointHistoryEntity history = PointHistoryEntity.builder()
                .student(student)
                .volunteerWork(work)
                .pointType(PointType.EARN)
                .points(points)
                .reason(String.format("'%s' 봉사활동 완료", work.getWorkName()))
                .build();

        pointHistoryRepository.save(history);

        // 학생의 총 포인트 및 횟수 증가
        student.setTotalPoints(student.getTotalPoints() + points);
        student.setTotalCount(student.getTotalCount() + 1);
        userRepository.save(student);

        log.info("학생 ID: {} - 포인트 {}점 획득 (봉사활동: {})",
                student.getId(), points, work.getWorkName());
    }

//   미참여 시 포인트 차감
    @Transactional
    public void deductPoints(UserEntity student, VolunteerWorkEntity work) {
        int deductPoints = calculatePoints(work.getDifficulty());

        // 포인트 내역 생성
        PointHistoryEntity history = PointHistoryEntity.builder()
                .student(student)
                .volunteerWork(work)
                .pointType(PointType.DEDUCT)
                .points(deductPoints)
                .reason(String.format("'%s' 봉사활동 미참여", work.getWorkName()))
                .build();

        pointHistoryRepository.save(history);

        // 학생의 총 포인트 차감 (음수가 되지 않도록)
        int newTotalPoints = Math.max(0, student.getTotalPoints() - deductPoints);
        student.setTotalPoints(newTotalPoints);
        userRepository.save(student);

        log.info("학생 ID: {} - 포인트 {}점 차감 (봉사활동: {})",
                student.getId(), deductPoints, work.getWorkName());
    }

//  학생의 포인트 내역 조회
    public List<PointHistoryResponse> getPointHistory(Long studentId) {
        List<PointHistoryEntity> histories = pointHistoryRepository.findAllByStudentId(studentId);

        return histories.stream()
                .map(PointHistoryResponse::from)
                .collect(Collectors.toList());
    }
}