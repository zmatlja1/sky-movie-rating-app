package uk.sky.pm.exception;

import lombok.Getter;
import uk.sky.pm.enums.ErrorCode;

@Getter
public class BusinessNotFoundException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessNotFoundException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

}
