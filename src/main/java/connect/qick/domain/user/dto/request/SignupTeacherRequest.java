package connect.qick.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "선생님 회원가입 요청 DTO")
public record SignupTeacherRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Schema(description = "선생님 이름", example = "홍길동")
        String name,

        @NotBlank(message = "선생님 코드는 필수입니다.")
        @Pattern(regexp = "^\\d{5}$", message = "선생님 코드는 5자리 숫자여야 합니다.")
        @Schema(description = "선생님 5자리 코드", example = "01234")
        String teacherCode
) {
}
