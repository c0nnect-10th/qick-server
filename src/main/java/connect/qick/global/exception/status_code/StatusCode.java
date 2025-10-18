package connect.qick.global.exception.status_code;

import org.springframework.http.HttpStatus;

public interface StatusCode {
  String getCode();
  String getMessage();
  HttpStatus getHttpStatus();
}