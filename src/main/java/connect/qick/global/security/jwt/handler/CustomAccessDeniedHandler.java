package connect.qick.global.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.data.ErrorResponse;
import connect.qick.global.util.ApiResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static connect.qick.domain.auth.exception.AuthStatusCode.ACCESS_DENIED;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiResponseWriter writer;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpStatus status = ACCESS_DENIED.getHttpStatus();
        ErrorResponse errorResponse = ErrorResponse.of(ACCESS_DENIED.getCode(), ACCESS_DENIED.getMessage());
        ApiResponse<Void> apiResponse = ApiResponse.error(status, errorResponse);

        writer.write(status, apiResponse, response);

    }
}
