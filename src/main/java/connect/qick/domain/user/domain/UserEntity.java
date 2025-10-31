package connect.qick.domain.user.domain;

import connect.qick.domain.user.enums.UserType;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@AllArgsConstructor
@Builder
public class UserEntity extends Base {

    @Column
    private String googleId;

    @Column
    private String email;

    @Column
    private String name;

    @Enumerated(value= EnumType.STRING)
    @Column(name = "user_type", nullable = false, updatable = false, insertable = false)
    private UserType userType;

    @Column(unique = true, nullable = false)
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

}


