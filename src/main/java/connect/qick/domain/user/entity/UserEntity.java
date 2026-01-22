package connect.qick.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private String fcmToken;

    @Column
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    @Builder.Default
    private UserType userType = UserType.USER;

    @JsonIgnore
    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    @Builder.Default
    private UserStatus userStatus = UserStatus.TEMP;

    @Column(unique = true)
    private String teacherCode;

    @Column
    private Integer grade;

    @Column(name = "class")
    private Integer classNumber;

    @Column
    private Integer number;

    @Column
    private int totalPoints;

    @Column
    private int totalCount;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy="teacher"
    )
    @Builder.Default
    private List<VolunteerWorkEntity> volunteerWorks = new ArrayList<>();

    //==비즈니스 로직==//
    public void checkGoogleId(String googleId) {
        if (!this.googleId.equals(googleId)) {
            throw new AuthException(AuthStatusCode.ACCESS_DENIED);
        }
    }

    public void checkIsTeacher() {
        if (userType != UserType.TEACHER) {
            throw new  AuthException(AuthStatusCode.ACCESS_ONLY_TEACHER);
        }
    }

    public void checkIsStudent() {
        if (userType != UserType.STUDENT) {
            throw new  AuthException(AuthStatusCode.ACCESS_ONLY_STUDENT);
        }
    }

    public String getClassroom() {
        return "" + grade + classNumber + number;
    }

    public void setClassroom(String classroom) {
        if (classroom.startsWith("0") || classroom.length() != 4) {
            throw new UserException(UserStatusCode.INVALID_CLASSROOM);
        }
        grade = Integer.parseInt(classroom.substring(0, 1));
        classNumber = Integer.parseInt(classroom.substring(1, 2));
        number = Integer.parseInt(classroom.substring(2));
    }

    public void updateUserProfile(UpdateStudentRequest request) {
        if (request.name() != null) this.name = request.name();
        if (request.classroom() != null) setClassroom(request.classroom());
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
        setClassroom(request.classroom());
    }


    public VolunteerApplicationEntity applyVolunteer(VolunteerWorkEntity work) {
        checkIsStudent();
        work.validateApplication();
        VolunteerApplicationEntity application = VolunteerApplicationEntity.builder()
            .volunteerWork(work)
            .status(ApplicationStatus.APPLIED)
            .appliedAt(LocalDateTime.now())
            .build();

        application.setStudent(this);
        work.addApplication(application);

        return application;
    }

    private void checkClassroom(String classroom) {
        if (classroom.length() != 4 || classroom.startsWith("0")) {
            throw new UserException(UserStatusCode.INVALID_CLASSROOM);
        }
    }

}


