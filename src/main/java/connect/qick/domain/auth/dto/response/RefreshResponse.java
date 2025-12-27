package connect.qick.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "토큰 재발급 응답 DTO")
public class RefreshResponse {
    @Schema(description = "새로 발급된 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
}
