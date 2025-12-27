package connect.qick.domain.user.exception;


import connect.qick.global.exception.exception.ApplicationException;
import connect.qick.global.exception.status_code.StatusCode;

public class UserException extends ApplicationException {

  public UserException(StatusCode statusCode) {
    super(statusCode);
  }

  public UserException(StatusCode statusCode, Throwable cause) {
    super(statusCode, cause);
  }

  public UserException(StatusCode statusCode, String message) {
    super(statusCode, message);
  }
}
