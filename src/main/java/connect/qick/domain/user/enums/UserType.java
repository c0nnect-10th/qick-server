package connect.qick.domain.user.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserType {
    STUDENT("ROLE_STUDENT"),
    TEACHER("ROLE_TEACHER");

    private final String key;
}
