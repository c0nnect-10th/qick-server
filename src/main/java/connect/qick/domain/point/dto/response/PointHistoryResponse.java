package connect.qick.domain.point.dto.response;

import connect.qick.domain.point.entity.PointHistoryEntity;
import connect.qick.domain.point.enums.PointType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "포인트 내역 응답 DTO")
public class PointHistoryResponse {

    @Schema(description = "포인트 내역 ID", example = "1")
    private Long id;

    @Schema(description = "포인트 타입", example = "EARN")
    private PointType pointType;

    @Schema(description = "포인트", example = "40")
    private Integer points;

    @Schema(description = "사유", example = "점심시간 교실 정리 봉사활동 완료")
    private String reason;

    @Schema(description = "봉사활동 ID", example = "5")
    private Long workId;

    @Schema(description = "봉사활동 이름", example = "점심시간 교실 정리")
    private String workName;

    @Schema(description = "생성 일시", example = "2025-12-27T14:30:00")
    private LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistoryEntity entity) {
        return new PointHistoryResponse(
                entity.getId(),
                entity.getPointType(),
                entity.getPoints(),
                entity.getReason(),
                entity.getVolunteerWork() != null ? entity.getVolunteerWork().getId() : null,
                entity.getVolunteerWork() != null ? entity.getVolunteerWork().getWorkName() : null,
                entity.getCreatedAt()
        );
    }
}