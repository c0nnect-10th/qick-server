package connect.qick.domain.teacher.domain;

import connect.qick.global.entity.Base;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access=  AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Table(name = "teacher")
@DiscriminatorValue("TEACHER")
public class Teacher extends Base {

    @Column(unique = true, nullable = false)
    private String teacher_code;

}
