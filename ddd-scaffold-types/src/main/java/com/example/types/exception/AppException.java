package com.example.types.exception;

import com.example.types.enums.ResponseCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常；由 trigger 层的全局异常处理器统一转换为 Response 返回
 */
@Getter
public class AppException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5317680961212299217L;

    /** 异常码 */
    private final String code;

    /** 异常信息 */
    private final String info;

    public AppException(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getInfo());
    }

    public AppException(ResponseCode responseCode, Throwable cause) {
        this(responseCode.getCode(), responseCode.getInfo(), cause);
    }

    public AppException(String code) {
        this(code, null, null);
    }

    public AppException(String code, Throwable cause) {
        this(code, null, cause);
    }

    public AppException(String code, String info) {
        this(code, info, null);
    }

    public AppException(String code, String info, Throwable cause) {
        super(info, cause);
        this.code = code;
        this.info = info;
    }

    @Override
    public String toString() {
        return "AppException{" +
                "code='" + code + '\'' +
                ", info='" + info + '\'' +
                '}';
    }

}
