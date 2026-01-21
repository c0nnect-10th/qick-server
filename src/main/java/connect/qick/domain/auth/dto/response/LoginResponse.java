package connect.qick.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "로그인 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String accessToken;
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String refreshToken;
    @Schema(description = "Signup Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    String signupToken;
}
