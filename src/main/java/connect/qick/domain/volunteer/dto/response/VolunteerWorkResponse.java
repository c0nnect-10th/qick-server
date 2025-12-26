package connect.qick.domain.volunteer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class VolunteerWorkResponse {
    private String workName;
    private String location;
    private String teacherName;
    private int maxParticipants;
    private int currentParticipants;
}
