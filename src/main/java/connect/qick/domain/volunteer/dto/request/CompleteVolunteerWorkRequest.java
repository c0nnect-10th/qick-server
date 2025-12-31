package connect.qick.domain.volunteer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "봉사활동 완료 처리 요청 DTO")
public record CompleteVolunteerWorkRequest(
        @NotNull(message = "참여 학생 ID 목록은 필수입니다.")
        @Schema(description = "실제 참여한 학생 ID 목록", example = "[1, 2, 3]")
        List<Long> attendedStudentIds
) {}