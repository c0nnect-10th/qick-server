package connect.qick.domain.volunteer.dto.response;

import connect.qick.domain.volunteer.enums.WorkStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "심부름 생성 후의 응답 DTO")
public class CreateVolunteerWorkResponse {
    @Schema(description = "생성된 심부름의 ID", example = "1")
    private Long id;
    @Schema(description = "생성된 심부름의 상태", example = "RECRUITING")
    private WorkStatus status;
}
