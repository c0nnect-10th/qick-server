package connect.qick.domain.volunteer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "봉사활동 신청 취소 요청 DTO")
public record CancelApplicationRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        @Schema(description = "취소 사유", example = "개인 사정으로 인한 취소")
        String cancelReason
) {}