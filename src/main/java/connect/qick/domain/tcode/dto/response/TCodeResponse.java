package connect.qick.domain.tcode.dto.response;

import connect.qick.domain.tcode.entity.TCodeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TCodeResponse {
    private Long id;
    private String code;
    private String teacherName;
    private boolean isUsed;

    public static TCodeResponse from(TCodeEntity entity) {
        return TCodeResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .teacherName(entity.getTeacherName())
                .isUsed(entity.isUsed())
                .build();
    }
}
