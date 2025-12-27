package connect.qick.domain.volunteer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Schema(description = "심부름 상세 정보 응답 DTO")
public class VolunteerWorkResponse {
    @Schema(description = "심부름 ID", example = "1")
    private Long id;
    @Schema(description = "심부름 이름", example = "점심시간 교실 정리")
    private String workName;
    @Schema(description = "심부름 난이도", example = "EASY")
    private WorkDifficulty difficulty;
    @Schema(description = "담당 선생님 이름", example = "김선생")
    private String teacherName;
    @Schema(description = "심부름 수행 장소", example = "1-1 교실")
    private String location;
    @Schema(description = "심부름 시작 시간", example = "2025-12-27T13:00:00")
    private LocalDateTime start_time;
    @Schema(description = "심부름 상세 설명", example = "점심시간에 교실을 깨끗하게 정리합니다.")
    private String description;
    @Schema(description = "최대 참여 인원", example = "5")
    private int maxParticipants;
    @Schema(description = "현재 참여 인원", example = "2")
    private int currentParticipants;
    @Schema(description = "심부름 상태", example = "RECRUITING")
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
