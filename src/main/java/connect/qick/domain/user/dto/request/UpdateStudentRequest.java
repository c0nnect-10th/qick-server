package connect.qick.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@Schema(description = "학생 정보 수정 요청 DTO")
public record UpdateStudentRequest(
        @Schema(description = "새로운 사용자 이름", example = "김길동")
        String name,

        @Schema(description = "새로운 학번", example = "1317")
        @Pattern(regexp = "^[1-9]\\d{3}$" , message = "학번의 형식이 일치하지 않습니다.")
        String classroom
) {}
