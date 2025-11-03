package connect.qick.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeacherSignUpRequest(

    @NotBlank(message = "Teacher 코드는 필수입니다.")
    String teacherCode,
    @NotBlank(message = "이름은 필수입니다.")
    String username

) {}
