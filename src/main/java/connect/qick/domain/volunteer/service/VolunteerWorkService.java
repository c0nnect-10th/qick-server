package connect.qick.domain.volunteer.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.entity.UserEntity;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserService userService;

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

        volunteerWork.setStatus(WorkStatus.CANCELLED);
    }
}
