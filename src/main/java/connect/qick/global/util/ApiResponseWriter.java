package connect.qick.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import connect.qick.global.data.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiResponseWriter {

    private final ObjectMapper mapper;

    public void write(HttpStatus status, ApiResponse<Void> apiResponse, HttpServletResponse response) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        String json = mapper.writeValueAsString(apiResponse);
        response.getWriter().write(json);
    }
}
