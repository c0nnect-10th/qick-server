package connect.qick.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String accessToken;
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String refreshToken;
    @Schema(description = "신규 유저 여부", example = "true")
    Boolean isNewUser;
}
