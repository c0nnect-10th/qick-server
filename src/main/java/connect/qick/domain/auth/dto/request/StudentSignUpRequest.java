package connect.qick.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StudentSignUpRequest(

    @NotBlank(message = "이름은")
    String username,

    @NotBlank(message = "google Id는 필수입니다.")
    String googleId,

    @NotBlank(message = "이메일은 필수입니다.")
    String email,

    int grade,

    int classNumber,

    int number

) {}
