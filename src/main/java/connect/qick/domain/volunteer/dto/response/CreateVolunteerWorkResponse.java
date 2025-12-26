package connect.qick.domain.volunteer.dto.response;

import connect.qick.domain.volunteer.enums.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateVolunteerWorkResponse {
    private Long id;
    private WorkStatus status;
}
