package connect.qick.domain.auth.dto.response;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserRole;
import jakarta.validation.constraints.NotBlank;

public record SignInResponse(

        UserEntity user,
        String accessToken,
        String refreshToken

) {}
