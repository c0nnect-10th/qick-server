package connect.qick.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserType {
    ADMIN("ROLE_ADMIN"),
    GUEST("ROLE_GUEST"),
    STUDENT("ROLE_STUDENT"),
    TEACHER("ROLE_TEACHER");

    private final String key;
}
