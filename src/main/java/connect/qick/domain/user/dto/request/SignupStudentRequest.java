package connect.qick.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "학생 회원가입 요청 DTO")
public record SignupStudentRequest(
   @NotBlank(message = "이름은 필수입니다.")
   @Schema(description = "사용자 이름", example = "홍길동")
   String name,
   @NotBlank(message = "학번은 필수입니다.")
   @Schema(description = "학번", example = "1317")
   String classroom
) {}
