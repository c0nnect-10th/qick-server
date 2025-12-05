package connect.qick.domain.user.service;

import connect.qick.domain.auth.exception.AuthException;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


@SpringBootTest()
class userServiceTest {

    @Autowired
    userService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void throwAuthException() {
        String tCode = UUID.randomUUID().toString();
        Assertions.assertThrows(AuthException.class, () -> {
            userService.getTeacher(tCode, null);
        });
    }

    @Test
    void saveTeacher() {
        String tCode = "abcd";
        String name = "안녕하시긔";

        UserEntity user = UserEntity.builder()
                .teacherCode(passwordEncoder.encode(tCode))
                .userType(UserType.TEACHER)
                .name(name)
                .build();

        userService.saveUser(user);

        UserEntity loadedUser = userService.getTeacher(tCode, name);

        Assertions.assertEquals(user.getName(), loadedUser.getName());
    }

}