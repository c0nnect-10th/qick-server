package connect.qick.domain.volunteer.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.point.service.PointService;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.*;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;;
    private final VolunteerApplicationRepository applicationRepository;
    private final UserService userService;
    private final PointService pointService;
    private final VolunteerApplicationRepository volunteerApplicationRepository;

    /**
     * 봉사활동 목록 조회
     * @return 봉사활동 요약 리스트 반환
     */
    public List<VolunteerWorkSummaryResponse> findAll() {
        return volunteerWorkRepository.findAllSummary();
    }

    /**
     * 특정 봉사활동 조회
     * @param id 봉사활동 id
     * @return 봉사활동 내용
     */
    public VolunteerWorkResponse findVolunteerWork(Long id) {
        return VolunteerWorkResponse.from(findById(id));
    }

    /**
     * 봉사활동 생성
     * @param googleId
     * @param request
     * @return 봉사활동 Id, 봉사활동 상태
     */
    public CreateVolunteerWorkResponse create(
            String googleId,
            CreateVolunteerWorkRequest request
    ) {
        UserEntity teacher =  userService.getUserByGoogleId(googleId)
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));

        VolunteerWorkEntity work = VolunteerWorkEntity.createVolunteerWork(teacher, request);
        volunteerWorkRepository.save(work);

        return new CreateVolunteerWorkResponse(work.getId(), work.getStatus());
    }

    /**
     * 봉사활동 삭제
     * @param workId
     * @param googleId
     */
    public void deleteVolunteerWork(Long workId, String googleId) {
        VolunteerWorkEntity work = volunteerWorkRepository.findByWorkId(workId)
            .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));
        work.getTeacher().checkGoogleId(googleId);
        work.cancel();
    }


    public CompleteVolunteerWorkResponse completeVolunteerWork(
            Long workId,
            List<Long> attendedStudentIds,
            String googleId
    ) {
        // 봉사활동 조회
        VolunteerWorkEntity work = findById(workId);
        work.getTeacher().checkGoogleId(googleId);

        // 상태 확인 (ONGOING 상태여야 완료 가능)
        if (work.getStatus() != WorkStatus.ONGOING) {
            throw new VolunteerException(VolunteerStatusCode.INVALID_WORK_STATUS);
        }

        // 해당 봉사활동의 모든 신청 내역 조회
        List<VolunteerApplicationEntity> applications = work.getApplications();

        int attendedCount = 0;
        int noShowCount = 0;

        // 출석 체크 및 상태 업데이트
        for (VolunteerApplicationEntity application : applications) {
            if (application.getStatus() == ApplicationStatus.APPLIED) {
                UserEntity student = application.getStudent();

                if (attendedStudentIds.contains(student.getId())) {
                    // 참여한 학생
                    application.complete();
                    // 포인트 지급
                    pointService.earnPoints(student, work);

                    attendedCount++;
                } else {
                    // 미참여 학생
                    application.notComplete();
                    // 포인트 차감
                    pointService.deductPoints(student, work);

                    noShowCount++;
                }
            }
        }

        // 봉사활동 상태를 COMPLETED로 변경
        work.complete();

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

    // 특정 봉사를 신청한 모든 학생 목록 조회
    public List<ApplicationStudentResponse> getApplicationStudents(Long workId, String googleId) {
        // 봉사활동 조회
        VolunteerWorkEntity work = findById(workId);
        work.getTeacher().checkGoogleId(googleId);

        // 신청자 목록 조회
        List<VolunteerApplicationEntity> applications = work.getApplications();
        return applications.stream()
                .filter(application -> application.getStatus() == ApplicationStatus.APPLIED)
                .map(ApplicationStudentResponse::from)
                .collect(Collectors.toList());
    }

    public VolunteerWorkEntity findById(Long id) {
        return volunteerWorkRepository.findById(id)
                .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));
    }

}
