package connect.qick.global.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    int status,
    T data,
    ErrorResponse error
) {
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(HttpStatus.OK.value(), data, null);
  }

  public static ApiResponse<Void> error(HttpStatus status, String code, String message) {
    return new ApiResponse<>(status.value(), null, ErrorResponse.of(code, message));
  }

  public static ApiResponse<Void> error(HttpStatus status, String code, String message, Map<String, String> details) {
    return new ApiResponse<>(status.value(), null, ErrorResponse.of(code, message, details));
  }

  public static ApiResponse<Void> error(HttpStatus status, ErrorResponse error) {
    return new ApiResponse<>(status.value(), null, error);
  }
}

