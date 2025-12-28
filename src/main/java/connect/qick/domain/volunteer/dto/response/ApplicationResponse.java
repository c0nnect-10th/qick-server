package connect.qick.domain.volunteer.dto.response;

import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "봉사활동 신청 정보 응답 DTO")
public class ApplicationResponse {
    @Schema(description = "신청 ID", example = "1")
    private Long applicationId;

    @Schema(description = "봉사활동 ID", example = "1")
    private Long workId;

    @Schema(description = "봉사활동 이름", example = "점심시간 교실 정리")
    private String workName;

    @Schema(description = "신청 상태", example = "APPLIED")
    private ApplicationStatus status;

    @Schema(description = "신청 일시", example = "2025-12-27T10:00:00")
    private LocalDateTime appliedAt;

    @Schema(description = "취소 사유", example = "개인 사정으로 인한 취소")
    private String cancelReason;

    @Schema(description = "취소 일시", example = "2025-12-27T11:00:00")
    private LocalDateTime cancelledAt;

    public static ApplicationResponse from(VolunteerApplicationEntity entity) {
        return new ApplicationResponse(
                entity.getId(),
                entity.getVolunteerWork().getId(),
                entity.getVolunteerWork().getWorkName(),
                entity.getStatus(),
                entity.getAppliedAt(),
                entity.getCancelReason(),
                entity.getCancelledAt()
        );
    }
}