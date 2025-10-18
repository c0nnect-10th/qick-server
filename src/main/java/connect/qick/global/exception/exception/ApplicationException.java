package connect.qick.global.exception.exception;

import connect.qick.global.exception.status_code.StatusCode;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
  private final StatusCode statusCode;

  public ApplicationException(StatusCode statusCode) {
    super(statusCode.getMessage());
    this.statusCode = statusCode;
  }

  public ApplicationException(StatusCode statusCode, Throwable cause) {
    super(statusCode.getMessage(), cause);
    this.statusCode = statusCode;
  }

  public ApplicationException(StatusCode statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public static ApplicationException of(StatusCode statusCode) {
    return new ApplicationException(statusCode);
  }

  public static ApplicationException of(StatusCode statusCode, Throwable cause) {
    return new ApplicationException(statusCode, cause);
  }

  public static ApplicationException of(StatusCode statusCode, String customMessage) {
    return new ApplicationException(statusCode, customMessage);
  }
}
