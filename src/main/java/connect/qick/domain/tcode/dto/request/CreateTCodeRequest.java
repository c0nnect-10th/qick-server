package connect.qick.domain.tcode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTCodeRequest {
    @NotBlank
    private String teacherName;
}
