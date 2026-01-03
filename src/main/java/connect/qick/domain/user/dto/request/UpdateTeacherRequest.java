package connect.qick.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

@Schema(description = "학생 정보 수정 요청 DTO")
public record UpdateTeacherRequest(
        @Schema(description = "새로운 사용자 이름", example = "김길동")
        String name
) {}
