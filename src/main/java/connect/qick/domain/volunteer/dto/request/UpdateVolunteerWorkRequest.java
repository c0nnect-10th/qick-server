package connect.qick.domain.volunteer.dto.request;

import connect.qick.domain.volunteer.enums.WorkDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "봉사활동 수정 요청 DTO")
public record UpdateVolunteerWorkRequest(
        @Schema(description = "봉사활동 이름", example = "점심시간 교실 정리")
        String name,

        @Positive(message = "최대 참여 인원은 1명 이상이어야 합니다.")
        @Schema(description = "최대 참여 인원", example = "10")
        Integer maxParticipants,

        @Schema(description = "봉사활동 장소", example = "1-1 교실")
        String location,

        @Schema(description = "봉사활동 상세 설명", example = "점심시간에 교실을 정리합니다.")
        String description,

        @Schema(description = "봉사활동 난이도", example = "EASY")
        WorkDifficulty difficulty,

        @Schema(description = "봉사활동 시작 시간", example = "2026-03-02T13:00:00")
        LocalDateTime startTime
) {
}
