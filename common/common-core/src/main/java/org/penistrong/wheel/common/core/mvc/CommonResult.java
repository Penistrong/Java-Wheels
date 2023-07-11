package org.penistrong.wheel.common.core.mvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MVC通用返回结果包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 10001234L;

    private HttpStatus code;
    private String message = "";
    public T data;
    private LocalDateTime timestamp;

    public static CommonResult<Void> ok() {
        return ok(null);
    }

    public static <T> CommonResult<T> ok(T data) {
        return new CommonResult<>(HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), data, LocalDateTime.now());
    }

    public static <T> CommonResult<T> ok(T data, String message) {
        return new CommonResult<>(HttpStatus.OK, message, data, LocalDateTime.now());
    }

    public static <T> CommonResult<T> error(HttpStatus errorCode, String message) {
        return new CommonResult<>(errorCode, message, null, LocalDateTime.now());
    }

    public boolean success() {
        return code == HttpStatus.OK;
    }
}
