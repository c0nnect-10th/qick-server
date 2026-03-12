package connect.qick.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "사용자 랭킹 정보 응답 DTO")
public class UserRankingResponse {

    @Schema(description = "랭킹", example = "1")
    private int ranking;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "gildong@qick.com")
    private String email;

    @Schema(description = "누적 봉사 점수", example = "15")
    private Integer totalPoints;

    @Schema(description = "학년 (학생 전용)", example = "2")
    private Integer grade;

    @Schema(description = "반 (학생 전용)", example = "3")
    private Integer classNumber;

    @Schema(description = "번호 (학생 전용)", example = "14")
    private Integer number;

    public static UserRankingResponse from(UserEntity user, int ranking) {
        UserRankingResponse response = new UserRankingResponse();
        response.ranking = ranking;
        response.name = user.getName();
        response.email = user.getEmail();
        response.totalPoints = user.getTotalPoints();
        if (user.getUserType() == UserType.STUDENT) {
            response.grade = user.getGrade();
            response.classNumber = user.getClassNumber();
            response.number = user.getNumber();
        }
        return response;
    }
}
