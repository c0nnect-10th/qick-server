package connect.qick.domain.student.domain;

import connect.qick.domain.user.enums.UserType;
import connect.qick.global.entity.Base;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access=  AccessLevel.PROTECTED)
@Getter
@Table(name = "student")
@DiscriminatorValue("STUDENT")
@AllArgsConstructor
@Builder
public class Student extends Base {

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
