package connect.qick.domain.teacher.domain;

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
@Table(name = "teacher")
@DiscriminatorValue("TEACHER")
@AllArgsConstructor
@Builder
public class Teacher extends Base {

    @Column(unique = true, nullable = false)
    private String teacher_code;

}
