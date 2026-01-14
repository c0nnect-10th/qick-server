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
            long currentAppliedCount = applicationRepository.countAppliedByWorkId(workId);
            if (currentAppliedCount >= work.getMaxParticipants()) {
                throw new VolunteerException(VolunteerStatusCode.RECRUITMENT_FULL);
            }

            // 신청 생성
            VolunteerApplicationEntity application = VolunteerApplicationEntity.builder()
                    .volunteerWork(work)
                    .student(student)
                    .status(ApplicationStatus.APPLIED)
                    .appliedAt(LocalDateTime.now())
                    .build();

            applicationRepository.save(application);

            // 현재 참여 인원 증가
            work.setCurrentParticipants(work.getCurrentParticipants() + 1);
            volunteerWorkRepository.save(work);

            return ApplicationResponse.from(application);
        }
    }

    public void cancelApplication(Long applicationId, String googleId, String cancelReason) {
        // 신청 내역 조회
        VolunteerApplicationEntity application = findById(applicationId);
        application.validateStudent(googleId);

        application.cancel(cancelReason);
    }

    public List<MyApplicationResponse> getMyApplications(String googleId) {
        List<VolunteerApplicationEntity> applications =
                applicationRepository.findAllByGoogleId(googleId);

        return applications.stream()
                .map(MyApplicationResponse::from)
                .collect(Collectors.toList());
    }

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