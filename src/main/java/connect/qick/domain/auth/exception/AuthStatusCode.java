package connect.qick.domain.auth.exception;

import connect.qick.global.exception.status_code.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthStatusCode implements StatusCode {
  INVALID_JWT("INVALID_JWT", "유효하지 않은 JWT입니다.", HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
  ACCOUNT_LOCKED("ACCOUNT_LOCKED", "계정이 잠겨 있습니다.", HttpStatus.UNAUTHORIZED),
  ACCOUNT_DISABLED("ACCOUNT_DISABLED", "계정이 비활성화되었습니다.", HttpStatus.UNAUTHORIZED),

  INVALID_T_CODE_EXCEPTION("INVALID_T_CODE_EXCEPTION", "유효하지 않은 T코드입니다.", HttpStatus.UNAUTHORIZED),
  ALREADY_EXISTS_EXCEPTION("ALREADY_EXISTS_EXCEPTION", "이미 가입된 사용자입니다.", HttpStatus.CONFLICT),
  UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
  ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다.",  HttpStatus.FORBIDDEN),;

  ;

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;
}
