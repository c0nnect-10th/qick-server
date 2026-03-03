package connect.qick.domain.tcode.exception;

import connect.qick.global.exception.status_code.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TCodeStatusCode implements StatusCode {
    TCODE_NOT_FOUND("TCODE_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 TCode입니다."),
    TCODE_ALREADY_USED("TCODE_ALREADY_USED", HttpStatus.CONFLICT, "이미 사용된 TCode입니다."),
    TCODE_NAME_MISMATCH("TCODE_NAME_MISMATCH", HttpStatus.BAD_REQUEST, "TCode에 할당된 교사 이름과 일치하지 않습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

}
