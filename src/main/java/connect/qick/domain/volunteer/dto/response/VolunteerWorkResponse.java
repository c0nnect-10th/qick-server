package connect.qick.domain.volunteer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class VolunteerWorkResponse {
    private Long id;
    private String workName;
    private WorkDifficulty difficulty;
    private String teacherName;
    private String location;
    private LocalDateTime start_time;
    private String description;
    private int maxParticipants;
    private int currentParticipants;
    private WorkStatus status;


    public static VolunteerWorkResponse from(VolunteerWorkEntity entity) {
        return new VolunteerWorkResponse(
            entity.getId(),
            entity.getWorkName(),
            entity.getDifficulty(),
            entity.getTeacher().getName(),
            entity.getLocation(),
            entity.getStart_time(),
            entity.getDescription(),
            entity.getMaxParticipants(),
            entity.getCurrentParticipants(),
            entity.getStatus()
        );
    }
}
