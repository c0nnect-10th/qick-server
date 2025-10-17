package connect.qick.domain.auth.exception;


import connect.qick.global.exception.exception.ApplicationException;
import connect.qick.global.exception.status_code.StatusCode;

public class AuthException extends ApplicationException {

  public AuthException(StatusCode statusCode) {
    super(statusCode);
  }

  public AuthException(StatusCode statusCode, Throwable cause) {
    super(statusCode, cause);
  }

  public AuthException(StatusCode statusCode, String message) {
    super(statusCode, message);
  }
}
