package connect.qick.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "구글 OAuth2 요청 DTO")
public record IdTokenRequest(
        @NotBlank(message="idToken은 필수입니다.")
        @Schema(description = "IdToken", example="eyJhbGciOiJSUzI1NiIsImtpZCI6IjZhOTA2ZWMxMTlkN2JhNDZhNmE0...")
        String idToken
)
{}
