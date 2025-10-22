package connect.qick.domain.auth.dto.response;

import connect.qick.domain.user.entity.Teacher;
import connect.qick.domain.user.entity.UserEntity;

public record TeacherLoginResponse(

        Teacher teacher,
        String accessToken,
        String refreshToken

) {}
