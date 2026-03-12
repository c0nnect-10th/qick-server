package connect.qick.domain.tcode.entity;

import connect.qick.global.entity.Base;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "t_code")
public class TCodeEntity extends Base {

    @Column(nullable = false, unique = true, length = 5)
    private String code;

    @Column(nullable = false)
    private String teacherName;

    @Column(nullable = false)
    private boolean isUsed = false;

    @Builder
    public TCodeEntity(String code, String teacherName) {
        this.code = code;
        this.teacherName = teacherName;
        this.isUsed = false;
    }

    public void use() {
        this.isUsed = true;
    }
}
