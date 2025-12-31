package connect.qick.domain.volunteer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "봉사활동 완료 처리 응답 DTO")
public class CompleteVolunteerWorkResponse {
    @Schema(description = "봉사활동 ID", example = "1")
    private Long workId;

    @Schema(description = "봉사활동 이름", example = "점심시간 교실 정리")
    private String workName;

    @Schema(description = "실제 참여 인원", example = "3")
    private int attendedCount;

    @Schema(description = "미참여 인원", example = "2")
    private int noShowCount;
}