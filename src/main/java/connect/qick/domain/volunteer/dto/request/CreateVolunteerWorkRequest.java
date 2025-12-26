package connect.qick.domain.volunteer.dto.request;

import connect.qick.domain.volunteer.enums.WorkDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateVolunteerWorkRequest(
   @NotBlank(message = "이름은 필수입니다.")
   String name,

   @NotNull(message = "모집 인원은 필수입니다.")
   Integer maxParticipants,

   @NotBlank(message = "모집 장소는 필수입니다.")
   String location,

   String description,

   @NotNull(message = "난이도는 필수입니다.")
   WorkDifficulty difficulty,

   @NotNull(message = "모집 시간을 필수입니다.")
   LocalDateTime start_time

) {}
