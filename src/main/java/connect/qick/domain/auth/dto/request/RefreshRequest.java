package connect.qick.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청 DTO")
public record RefreshRequest(
        @NotBlank(message="refreshToken은 필수입니다.")
        @Schema(description = "Refresh Token", example="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29nbGUg...")
        String refreshToken
)
{}
