package connect.qick.domain.auth.dto.response;

import connect.qick.domain.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResolveResult {
    Boolean isNewUser;
    UserEntity user;

    public UserResolveResult(UserEntity user) {
        this.user = user;
        this.isNewUser = false;
    }
}
