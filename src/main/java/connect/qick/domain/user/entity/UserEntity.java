package connect.qick.domain.user.entity;

import connect.qick.domain.user.dto.request.UpdateUserRequest;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.User;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity extends Base {

    @Column
    private String googleId;

    @Column
    private String email;

    @Column
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_type", nullable = false, updatable = false)
    private UserType userType;

    @Column(unique = true)
    private String teacherCode;

    @Column
    private int grade;

    @Column(name = "class")
    private int classNumber;

    @Column
    private int number;

    @Column
    private int totalPoints;

    @Column
    private int totalCount;

    public void updateUserProfile(UpdateUserRequest request) {
        if (request.name() != null) this.name = request.name();
        if (request.classroom() != null) {
            String classroom = request.classroom();
            if (classroom.startsWith("0") || classroom.length() != 4) {
                throw new UserException(UserStatusCode.INVALID_CLASSROOM);
            }
            String grade = classroom.substring(0, 1);
            String classNumber = classroom.substring(1, 2);
            String number = classroom.substring(2);

            this.grade = Integer.parseInt(grade);
            this.classNumber = Integer.parseInt(classNumber);
            this.number = Integer.parseInt(number);

        }

    }

}


