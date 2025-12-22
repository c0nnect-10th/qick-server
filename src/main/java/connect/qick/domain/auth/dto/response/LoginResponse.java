package connect.qick.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    String accessToken;
    String refreshToken;
    Boolean isNewUser;
}
