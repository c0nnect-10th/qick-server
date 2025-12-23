package connect.qick.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String name;
    String email;
    Integer totalPoints;
//    Integer ranking;
    Integer totalCount;
    Integer grade;
    Integer classNumber;
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
