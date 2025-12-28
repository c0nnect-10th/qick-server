package connect.qick.domain.volunteer.exception;

import connect.qick.global.exception.status_code.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VolunteerStatusCode implements StatusCode {
  WORK_NOT_FOUND("WORK_NOT_FOUND", "심부름이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  WORK_CANCELLED("WORK_CANCELLED", "삭제된 심부름 입니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;
}
