package connect.qick.domain.volunteer.dto.request;

import connect.qick.domain.volunteer.enums.WorkDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "새로운 심부름 생성을 위한 요청 DTO")
public record CreateVolunteerWorkRequest(
   @NotBlank(message = "이름은 필수입니다.")
   @Schema(description = "심부름 이름", example = "점심시간 교실 정리")
   String name,

   @NotNull(message = "모집 인원은 필수입니다.")
   @Schema(description = "최대 참여 인원", example = "5")
   Integer maxParticipants,

   @NotBlank(message = "모집 장소는 필수입니다.")
   @Schema(description = "심부름 수행 장소", example = "1-1 교실")
   String location,

   @Schema(description = "심부름 상세 설명", example = "점심시간에 교실을 깨끗하게 정리합니다.")
   String description,

   @NotNull(message = "난이도는 필수입니다.")
   @Schema(description = "심부름 난이도", example = "EASY")
   WorkDifficulty difficulty,

   @NotNull(message = "모집 시간을 필수입니다.")
   @Schema(description = "심부름 시작 시간", example = "2025-12-27T13:00:00")
   LocalDateTime start_time

) {}
