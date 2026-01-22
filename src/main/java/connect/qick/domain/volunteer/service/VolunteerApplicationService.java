package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.response.ApplicationResponse;
import connect.qick.domain.volunteer.dto.response.MyApplicationResponse;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import connect.qick.global.util.PushAlarmUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VolunteerApplicationService {

    private final VolunteerApplicationRepository applicationRepository;
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserService userService;
    private final PushAlarmUtil pushAlarmUtil;

    public ApplicationResponse applyToVolunteer(Long workId, String googleId) {
        // 학생 정보 조회
        UserEntity student = userService.getUserByGoogleId(googleId);
        VolunteerWorkEntity work = volunteerWorkRepository.findById(workId)
                .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));

        // 이미 신청했는지 확인
        if (applicationRepository.existsByVolunteerWorkIdAndStudentIdAndStatus(
                workId, student.getId(), ApplicationStatus.APPLIED)) {
            throw new VolunteerException(VolunteerStatusCode.ALREADY_APPLIED);
        }

        // 모집 인원 확인 (동시성 고려)
        synchronized (this) {
            VolunteerApplicationEntity application = student.applyVolunteer(work);

            // 선생님에게 푸시 알림 전송
            UserEntity teacher = work.getTeacher();
            if (teacher != null && teacher.getFcmToken() != null && !teacher.getFcmToken().isEmpty()) {
                String title = "새로운 봉사활동 신청";
                String body = String.format("%s 학생이 '%s' 봉사활동을 신청했습니다.", student.getName(), work.getWorkName());
                pushAlarmUtil.send(teacher.getFcmToken(), title, body);
            }

            return ApplicationResponse.from(application);
        }
    }

    /**
     * 사용자(학생)가 참여한 봉사활동을 취소
     * @param applicationId 참여한 봉사활동 내역 Id
     * @param googleId 유저(학생)의 구글 Id
     * @param cancelReason 취소 사유
     */
    public void cancelApplication(Long applicationId, String googleId, String cancelReason) {
        VolunteerApplicationEntity application = findById(applicationId);

        application.cancel(cancelReason, googleId);
    }

    /**
     * 사용자(학생)가 참여한 봉사활동 목록 조회
     * @param googleId 유저(학생)의 구글 Id
     * @return List<MyApplicationResponse>
     */
    public List<MyApplicationResponse> getMyApplications(String googleId) {
        List<VolunteerApplicationEntity> applications =
                applicationRepository.findAllByGoogleId(googleId);

        return applications.stream()
                .map(MyApplicationResponse::from)
                .collect(Collectors.toList());
    }


    /**
     * 봉사활동 참여의 세부 사항
     * @param applicationId 참여한 봉사활동 내역 Id
     * @param googleId 사용자(학생)의 구글 Id
     * @return ApplicationResponse
     */
    public ApplicationResponse getApplicationDetail(Long applicationId, String googleId) {
        VolunteerApplicationEntity application = findById(applicationId);
        application.validateStudent(googleId); //TODO: applicationId, googleId 함께 조회하도록

        return ApplicationResponse.from(application);
    }

    public VolunteerApplicationEntity findById(Long applicationId) {
        return applicationRepository.findById(applicationId)
            .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.APPLICATION_NOT_FOUND));
    }
}