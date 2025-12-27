package connect.qick.domain.volunteer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Schema(description = "심부름 요약 정보 응답 DTO")
public class VolunteerWorkSummaryResponse {
    @Schema(description = "심부름 ID", example = "1")
    private Long id;
    @Schema(description = "심부름 이름", example = "점심시간 교실 정리")
    private String workName;
    @Schema(description = "심부름 수행 장소", example = "1-1 교실")
    private String location;
    @Schema(description = "담당 선생님 이름", example = "김선생")
    private String teacherName;
    @Schema(description = "최대 참여 인원", example = "5")
    private int maxParticipants;
    @Schema(description = "현재 참여 인원", example = "2")
    private int currentParticipants;
}
