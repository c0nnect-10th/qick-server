package connect.qick.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserType {
    STUDENT("ROLE_STUDENT"),
    TEACHER("ROLE_TEACHER");

    private final String key;
}
