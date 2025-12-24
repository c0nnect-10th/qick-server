package connect.qick.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponse {
    @Schema(description = "이름", example = "홍길동")
    String name;
    @Schema(description = "이메일", example = "gildong@qick.com")
    String email;
    @Schema(description = "누적 봉사 점수 (학생 전용)", example = "15")
    Integer totalPoints;
//    Integer ranking;
    @Schema(description = "누적 봉사 횟수 (학생 전용)", example = "5")
    Integer totalCount;
    @Schema(description = "학년 (학생 전용)", example = "2")
    Integer grade;
    @Schema(description = "반 (학생 전용)", example = "3")
    Integer classNumber;
    @Schema(description = "번호 (학생 전용)", example = "14")
    Integer number;

    public static UserResponse from(UserEntity user) {
        UserResponse response = new UserResponse();
        response.name = user.getName();
        response.email = user.getEmail();
        if (user.getUserType() == UserType.STUDENT) {
            response.totalPoints = user.getTotalPoints();
            response.totalCount = user.getTotalCount();
            response.grade = user.getGrade();
            response.classNumber = user.getClassNumber();
            response.number = user.getNumber();
        }
        return response;
    }
}
