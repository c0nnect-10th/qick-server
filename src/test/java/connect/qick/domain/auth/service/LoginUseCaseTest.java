package connect.qick.domain.auth.service;

import connect.qick.domain.auth.exception.AuthException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoginUseCaseTest {

    @Autowired
    private LoginUseCase loginUseCase;

    @Test
    void assertAuthException() {
        String name = "엄준식";
        String tCode = "abcd";

        Assertions.assertThrows(AuthException.class, () -> {
            loginUseCase.login(tCode, name);
        });
    }

    @Test
    void testTeacherLogin() {
        String name = "안녕하시긔";
        String tCode = "abcd";

        LoginResponse response = loginUseCase.login(tCode, name);
        System.out.println("\n\naccess Token: " + response.accessToken() + "\nrefresh Token: " + response.refreshToken());

    }

}