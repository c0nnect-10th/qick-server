package connect.qick.domain.volunteer.exception;

import connect.qick.global.exception.status_code.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VolunteerStatusCode implements StatusCode {
  WORK_NOT_FOUND("WORK_NOT_FOUND", "봉사활동이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "신청 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  ALREADY_APPLIED("ALREADY_APPLIED", "이미 신청한 봉사활동입니다.", HttpStatus.CONFLICT),
  RECRUITMENT_FULL("RECRUITMENT_FULL", "모집 인원이 마감되었습니다.", HttpStatus.CONFLICT),
  CANNOT_CANCEL("CANNOT_CANCEL", "취소할 수 없는 상태입니다.", HttpStatus.BAD_REQUEST),
  ;

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;
}