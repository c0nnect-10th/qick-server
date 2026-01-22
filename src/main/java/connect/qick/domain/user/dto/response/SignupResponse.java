package connect.qick.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "회원가입 성공 DTO")
public class SignupResponse {
    @Schema(description = "발급된 Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "발급된 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
