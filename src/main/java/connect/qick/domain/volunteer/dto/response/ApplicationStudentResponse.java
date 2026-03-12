package connect.qick.domain.volunteer.dto.response;

import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "봉사활동 신청자 정보 응답 DTO")
public class ApplicationStudentResponse {

    @Schema(description = "신청 ID", example = "1")
    private Long applicationId;

    @Schema(description = "학생 ID", example = "5")
    private Long studentId;

    @Schema(description = "학생 이름", example = "홍길동")
    private String studentName;

    @Schema(description = "학년", example = "2")
    private Integer grade;

    @Schema(description = "반", example = "3")
    private Integer classNumber;

    @Schema(description = "번호", example = "14")
    private Integer number;

    @Schema(description = "신청 상태", example = "APPLIED")
    private ApplicationStatus status;

    @Schema(description = "신청 일시", example = "2025-12-27T10:30:00")
    private LocalDateTime appliedAt;

    public static ApplicationStudentResponse from(VolunteerApplicationEntity entity) {
        return new ApplicationStudentResponse(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getStudent().getName(),
                entity.getStudent().getGrade(),
                entity.getStudent().getClassNumber(),
                entity.getStudent().getNumber(),
                entity.getStatus(),
                entity.getAppliedAt()
        );
    }
}