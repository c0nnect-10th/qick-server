package connect.qick.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record RefreshRequest(
        @NotNull(message="refreshToken은 필수입니다.")
        String refreshToken
)
{}
