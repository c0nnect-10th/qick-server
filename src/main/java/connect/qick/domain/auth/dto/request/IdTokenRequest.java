package connect.qick.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record IdTokenRequest(
        @NotBlank(message="idToken은 필수입니다.")
        String idToken
)
{}
