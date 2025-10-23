package connect.qick.global.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.qick.domain.auth.exception.AuthStatusCode;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.data.ErrorResponse;
import connect.qick.global.util.ApiResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import static connect.qick.domain.auth.exception.AuthStatusCode.UNAUTHORIZED;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;


@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiResponseWriter writer;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpStatus status = UNAUTHORIZED.getHttpStatus();;
        ErrorResponse errorResponse = ErrorResponse.of(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        ApiResponse<Void> apiResponse = ApiResponse.error(status, errorResponse);

        writer.write(status, apiResponse, response);

    }
}
