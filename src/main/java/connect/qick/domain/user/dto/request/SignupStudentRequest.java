package connect.qick.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignupStudentRequest(
   @NotBlank(message = "이름은 필수입니다.")
   String name,
   @NotBlank(message = "학번은 필수입니다.")
   String classroom
) {}
