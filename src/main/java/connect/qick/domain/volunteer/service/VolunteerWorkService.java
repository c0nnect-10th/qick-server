package connect.qick.domain.volunteer.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.point.service.PointService;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.response.CompleteVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;;
    private final VolunteerApplicationRepository applicationRepository;
    private final UserService userService;
    private final PointService pointService;

    public List<VolunteerWorkSummaryResponse> findAll() {
        return volunteerWorkRepository.findAllSummary();
    }

    public CreateVolunteerWorkResponse create(
            String workName,
            int maxParticipants,
            String location,
            String description,
            WorkDifficulty difficulty,
            LocalDateTime startTime,
            String googleId
    ) {
        UserEntity teacher =  userService.getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));
        VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                .workName(workName)
                .maxParticipants(maxParticipants)
                .currentParticipants(0)
                .location(location)
                .status(WorkStatus.RECRUITING)
                .description(description)
                .difficulty(difficulty)
                .startTime(startTime)
                .teacher(teacher)
                .build();
        volunteerWorkRepository.save(work);

        return new CreateVolunteerWorkResponse(work.getId(), work.getStatus());
    }

    public VolunteerWorkEntity findById(Long id) {
        return volunteerWorkRepository.findById(id)
                .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));
    }

    public VolunteerWorkEntity findByCanceledId(Long id) {
        Optional<VolunteerWorkEntity> opt = volunteerWorkRepository.findById(id);
        if (opt.isPresent()) {
            if (opt.get().getStatus() != WorkStatus.CANCELLED) {
                return opt.get();
            }
            throw new VolunteerException(VolunteerStatusCode.CANNOT_CANCEL);
        }
        throw new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND);
    }

    public VolunteerWorkResponse findVolunteerWork(Long id) {
        return VolunteerWorkResponse.from(findById(id));
    }

    public void deleteVolunteerWork(Long workId, String googleId) {
        VolunteerWorkEntity volunteerWork = findByCanceledId(workId);
        if (!volunteerWork.getTeacher().getGoogleId().equals(googleId)) {
            throw new AuthException(AuthStatusCode.ACCESS_DENIED);
        }

        volunteerWork.setStatus(WorkStatus.CANCELLED);
    }

    @Transactional
    public CompleteVolunteerWorkResponse completeVolunteerWork(
            Long workId,
            List<Long> attendedStudentIds,
            String googleId
    ) {
        // 봉사활동 조회
        VolunteerWorkEntity work = findById(workId);

        // 권한 확인 (본인이 생성한 봉사활동인지)
        if (!work.getTeacher().getGoogleId().equals(googleId)) {
            throw new AuthException(AuthStatusCode.ACCESS_DENIED);
        }

        // 상태 확인 (ONGOING 상태여야 완료 가능)
        if (work.getStatus() != WorkStatus.ONGOING) {
            throw new VolunteerException(VolunteerStatusCode.INVALID_WORK_STATUS);
        }

        // 해당 봉사활동의 모든 신청 내역 조회
        List<VolunteerApplicationEntity> applications =
                applicationRepository.findAllByVolunteerWorkId(workId);

        int attendedCount = 0;
        int noShowCount = 0;

        // 출석 체크 및 상태 업데이트
        for (VolunteerApplicationEntity application : applications) {
            if (application.getStatus() == ApplicationStatus.APPLIED) {
                Long studentId = application.getStudent().getId();
                UserEntity student = application.getStudent();

                if (attendedStudentIds.contains(studentId)) {
                    // 참여한 학생
                    application.setStatus(ApplicationStatus.COMPLETED);
                    application.setIsAttended(true);
                    application.setCompletedAt(LocalDateTime.now());

                    // 포인트 지급
                    pointService.earnPoints(student, work);

                    attendedCount++;
                } else {
                    // 미참여 학생
                    application.setStatus(ApplicationStatus.NO_SHOW);
                    application.setIsAttended(false);

                    // 포인트 차감
                    pointService.deductPoints(student, work);

                    noShowCount++;
                }
            }
        }

        // 신청 내역 저장
        applicationRepository.saveAll(applications);

        // 봉사활동 상태를 COMPLETED로 변경
        work.setStatus(WorkStatus.COMPLETED);
        volunteerWorkRepository.save(work);

        return new CompleteVolunteerWorkResponse(
                work.getId(),
                work.getWorkName(),
                attendedCount,
                noShowCount
        );
    }

    // 선생님이 생성한 봉사활동 목록 조회 (상태별)
    public List<VolunteerWorkEntity> getMyVolunteerWorks(String googleId, WorkStatus status) {
        UserEntity teacher = userService.getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));

        if (status != null) {
            return volunteerWorkRepository.findByTeacherIdAndStatus(teacher.getId(), status);
        } else {
            return volunteerWorkRepository.findAllByTeacherId(teacher.getId());
        }
    }

}
