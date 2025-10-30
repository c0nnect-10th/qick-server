package connect.qick.domain.user.domain;

import connect.qick.domain.user.enums.UserType;
import connect.qick.global.entity.Base;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@SuperBuilder
public class UserEntity extends Base {

    @Column
    private String googleId;

    @Column
    private String email;

    @Column
    private String name;

    @Enumerated(value= EnumType.STRING)
    private UserType userType;

}


