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
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserEntity extends Base {

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String email;

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
    private int totalPoints;

    @Column
    private int totalCount;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VolunteerWorkEntity> volunteerWorks = new ArrayList<>();

    public void updateUserProfile(UpdateStudentRequest request) {
        if (request.name() != null) this.name = request.name();
        if (request.classroom() != null) {
            String classroom = request.classroom();
            if (classroom.startsWith("0") || classroom.length() != 4) {
                throw new UserException(UserStatusCode.INVALID_CLASSROOM);
            }
            this.grade = Integer.parseInt(classroom.substring(0, 1));
            this.classNumber = Integer.parseInt(classroom.substring(1, 2));
            this.number = Integer.parseInt(classroom.substring(2));
        }
    }
    public void updateUserProfile(SignupStudentRequest request) {
        this.name = request.name();
        String classroom = request.classroom();
        if (classroom.startsWith("0") || classroom.length() != 4) {
            throw new UserException(UserStatusCode.INVALID_CLASSROOM);
        }
        this.grade = Integer.parseInt(classroom.substring(0, 1));
        this.classNumber = Integer.parseInt(classroom.substring(1, 2));
        this.number = Integer.parseInt(classroom.substring(2));
    }

}


