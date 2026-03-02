package connect.qick.domain.tcode.exception;

import connect.qick.global.exception.exception.ApplicationException;
import connect.qick.global.exception.status_code.StatusCode;

public class TCodeException extends ApplicationException {

    public TCodeException(StatusCode statusCode) {
        super(statusCode);
    }
}
