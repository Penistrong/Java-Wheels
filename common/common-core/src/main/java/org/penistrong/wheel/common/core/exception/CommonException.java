package org.penistrong.wheel.common.core.exception;

import lombok.Data;
import lombok.Getter;

@Data
public class CommonException extends RuntimeException{

    private static final long serialVersionUID = -1605856181815216874L;

    @Getter
    private int errorCode;
    private String message;

    public CommonException() {
    }

    public CommonException(final ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public CommonException(final ErrorCode errorCode, Exception e) {
        super(e);
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
    public CommonException(final ErrorCode errorCode,String massage) {
        this.errorCode = errorCode.getCode();
        this.message = massage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
