package connect.qick.domain.volunteer.exception;


import connect.qick.global.exception.exception.ApplicationException;
import connect.qick.global.exception.status_code.StatusCode;

public class VolunteerException extends ApplicationException {

  public VolunteerException(StatusCode statusCode) {
    super(statusCode);
  }

  public VolunteerException(StatusCode statusCode, Throwable cause) {
    super(statusCode, cause);
  }

  public VolunteerException(StatusCode statusCode, String message) {
    super(statusCode, message);
  }
}
