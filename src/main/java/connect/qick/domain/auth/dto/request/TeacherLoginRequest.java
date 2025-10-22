package connect.qick.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeacherLoginRequest(

        @NotBlank(message = "T 코드는 필수입니다.")
        String tCode,
        @NotBlank(message = "이름은 필수입니다.")
        String username
) {}
