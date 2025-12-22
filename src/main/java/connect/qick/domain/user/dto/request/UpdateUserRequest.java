package connect.qick.domain.user.dto.request;

public record UpdateUserRequest(
        String name,
        Integer grade,
        Integer classNumber,
        Integer number
) {}
