package connect.qick.domain.user.dto.request;

import org.hibernate.validator.constraints.Length;

public record UpdateStudentRequest(
        String name,

        @Length(min=4, max = 4)
        String classroom
) {}
