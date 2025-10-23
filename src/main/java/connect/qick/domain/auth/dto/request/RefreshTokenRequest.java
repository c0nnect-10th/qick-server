package connect.qick.domain.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RefreshTokenRequest(
  String refreshToken
) { }
