package connect.qick.domain.volunteer.dto.response;

import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "내가 신청한 봉사활동 정보 응답 DTO")
public class MyApplicationResponse {
    @Schema(description = "신청 ID", example = "1")
    private Long applicationId;

    @Schema(description = "봉사활동 ID", example = "1")
    private Long workId;

    @Schema(description = "봉사활동 이름", example = "점심시간 교실 정리")
    private String workName;

    @Schema(description = "담당 선생님 이름", example = "김선생")
    private String teacherName;

    @Schema(description = "봉사활동 장소", example = "1-1 교실")
    private String location;

    @Schema(description = "봉사활동 시작 시간", example = "2025-12-27T13:00:00")
    private LocalDateTime startTime;

    @Schema(description = "난이도", example = "EASY")
    private WorkDifficulty difficulty;

    @Schema(description = "신청 상태", example = "APPLIED")
    private ApplicationStatus status;

    @Schema(description = "신청 일시", example = "2025-12-27T10:00:00")
    private LocalDateTime appliedAt;

    public static MyApplicationResponse from(VolunteerApplicationEntity entity) {
        return new MyApplicationResponse(
                entity.getId(),
                entity.getVolunteerWork().getId(),
                entity.getVolunteerWork().getWorkName(),
                entity.getVolunteerWork().getTeacher().getName(),
                entity.getVolunteerWork().getLocation(),
                entity.getVolunteerWork().getStart_time(),
                entity.getVolunteerWork().getDifficulty(),
                entity.getStatus(),
                entity.getAppliedAt()
        );
    }
}