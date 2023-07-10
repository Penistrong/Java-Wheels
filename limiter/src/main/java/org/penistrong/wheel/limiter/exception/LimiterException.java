package org.penistrong.wheel.limiter.exception;

import lombok.Data;

@Data
public class LimiterException extends RuntimeException {

    public LimiterException(String msg) {
        super(msg);
    }

    public LimiterException(Exception e) {
        super(e);
    }

    public LimiterException(String msg, Exception e) {
        super(msg, e);
    }
}
