package connect.qick.domain.user.exception;

import connect.qick.global.exception.status_code.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserStatusCode implements StatusCode {
  NOT_FOUND("NOT_FOUND", "유저가 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
  ;

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;
}
