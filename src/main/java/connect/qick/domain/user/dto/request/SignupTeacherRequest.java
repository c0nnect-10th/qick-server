package connect.qick.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "학생 회원가입 요청 DTO")
public record SignupTeacherRequest(
   @NotBlank(message = "이름은 필수입니다.")
   @Schema(description = "사용자 이름", example = "홍길동")
   String name,
   @NotNull(message = "t코드는 필수입니다.")
   @Schema(description = "t코드", example = "1A2B-3C4D-5E6F-7G8H")
   String tCode
) {}
