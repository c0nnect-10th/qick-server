package connect.qick.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity extends Base {

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String email;

    @Column
    @Setter
    private String fcmToken;

    @Column
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.USER;

    @JsonIgnore
    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.TEMP;

    @Column(unique = true)
    private String teacherCode;

    @Column
    private int grade;

    @Column(name = "class")
    private int classNumber;

    @Column
    private int number;

    @Column
    @Setter
    private int totalPoints;

    @Column
    @Setter
    private int totalCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="teacher")
    private List<VolunteerWorkEntity> volunteerWorks = new ArrayList<>();

    public void updateUserProfile(UpdateStudentRequest request) {
        if (request.name() != null) this.name = request.name();
        if (request.classroom() != null && !request.classroom().startsWith("0")) {
            String classroom = request.classroom();
            checkClassroom(classroom);

            this.grade = Integer.parseInt(classroom.substring(0, 1));
            this.classNumber = Integer.parseInt(classroom.substring(1, 2));
            this.number = Integer.parseInt(classroom.substring(2));
        }
    }
    public void signupStudent(SignupStudentRequest request) {
        String classroom = request.classroom();
        checkClassroom(classroom);

        this.name = request.name();
        this.userType = UserType.STUDENT;
        this.userStatus = UserStatus.ACTIVE;
        this.grade = Integer.parseInt(classroom.substring(0, 1));
        this.classNumber = Integer.parseInt(classroom.substring(1, 2));
        this.number = Integer.parseInt(classroom.substring(2));
    }

    private void checkClassroom(String classroom) {
        if (classroom.length() != 4 || classroom.startsWith("0")) {
            throw new UserException(UserStatusCode.INVALID_CLASSROOM);
        }
    }

}


