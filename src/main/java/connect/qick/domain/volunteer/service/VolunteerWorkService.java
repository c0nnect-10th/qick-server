package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
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

    public List<VolunteerWorkResponse> findAll() {
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
}
