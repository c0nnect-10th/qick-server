package connect.qick.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.qick.domain.auth.exception.AuthException;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.data.ErrorResponse;
import connect.qick.global.util.ApiResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ApiResponseWriter writer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (AuthException e) {
            handleAuthException(e.getStatusCode().getHttpStatus(), response, e);
        }
        catch (ServletException e) {
//            handleAuthException(HttpStatus.BAD_REQUEST, response, e);
        }
    }

    public void handleAuthException(
                HttpStatus status,
                HttpServletResponse response,
                AuthException ex
    ) throws IOException {

        ErrorResponse errorResponse = ErrorResponse.of(ex.getStatusCode().getCode(), ex.getMessage());
        ApiResponse<Void> apiResponse = ApiResponse.error(status, errorResponse);

        writer.write(status, apiResponse, response);
    }


}

/*
* {
    "status": 400,
    "error": {
        "code": "INVALID_ARGUMENT",
        "message": "요청값이 유효하지 않습니다.",
        "timestamp": "2025-10-22T20:25:24.678284",
        "details": {
            "tCode": "T 코드는 필수입니다."
        }
    }
}
*
*
* */
