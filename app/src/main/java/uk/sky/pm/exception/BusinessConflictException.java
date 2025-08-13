package uk.sky.pm.exception;

import uk.sky.pm.enums.ErrorCode;

public class BusinessConflictException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessConflictException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
