package connect.qick.domain.user.entity;

import connect.qick.domain.user.dto.request.UpdateUserRequest;
import connect.qick.domain.user.enums.UserType;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
        if (request.grade() != null) this.grade = request.grade();
        if (request.classNumber() != null) this.classNumber = request.classNumber();
        if (request.number() != null) this.number = request.number();
    }

}


