package connect.qick.domain.volunteer.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType; // Added import
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import connect.qick.global.util.PushAlarmUtil; // Added import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // Added import

@RequiredArgsConstructor
@Service
public class VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserService userService;
    private final PushAlarmUtil pushAlarmUtil; // Added injection

    public List<VolunteerWorkSummaryResponse> findAll() {
        return volunteerWorkRepository.findAllSummary();
    }

    public CreateVolunteerWorkResponse create(
            String workName,
            int maxParticipants,
            String location,
            String description,
            WorkDifficulty difficulty,
            LocalDateTime start_time,
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
                .start_time(start_time)
                .teacher(teacher)
                .build();
        volunteerWorkRepository.save(work);

        // 푸쉬 알림 시작
        List<String> studentFcmTokens = userService.getUsersByUserType(UserType.STUDENT)
                .stream()
                .map(UserEntity::getFcmToken)
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        if (!studentFcmTokens.isEmpty()) {
            String title = "새로운 봉사활동: " + work.getWorkName();
            String body = work.getDescription().length() > 50 ?
                          work.getDescription().substring(0, 50) + "..." :
                          work.getDescription();
            pushAlarmUtil.sendMulticast(studentFcmTokens, title, body);
        }
        // 끝

        return new CreateVolunteerWorkResponse(work.getId(), work.getStatus());
    }

    public VolunteerWorkEntity findById(Long id) {
        return volunteerWorkRepository.findById(id)
                .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));
    }

    public VolunteerWorkResponse findVolunteerWork(Long id) {
        return VolunteerWorkResponse.from(findById(id));
    }

    public void deleteVolunteerWork(Long workId, String googleId) {
        VolunteerWorkEntity volunteerWork = findById(workId);
        if (!volunteerWork.getTeacher().getGoogleId().equals(googleId)) {
            throw new AuthException(AuthStatusCode.ACCESS_DENIED);
        }

        volunteerWorkRepository.delete(volunteerWork);
    }
}
