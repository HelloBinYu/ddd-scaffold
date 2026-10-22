package com.example.trigger.http;

import com.example.api.response.Response;
import com.example.types.enums.ResponseCode;
import com.example.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理；将异常统一转换为 Response 结构返回
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Response<Void> handleAppException(AppException e) {
        log.warn("业务异常 code:{} info:{}", e.getCode(), e.getInfo(), e);
        return Response.failure(e.getCode(), e.getInfo());
    }

    /** @RequestBody / 表单对象的 @Valid 校验失败（MethodArgumentNotValidException 是 BindException 的子类） */
    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException e) {
        String info = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return illegalParameter(info);
    }

    /** 方法参数上的 @NotBlank 等约束校验失败 */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public Response<Void> handleMethodValidationException(HandlerMethodValidationException e) {
        String info = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return illegalParameter(info);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    public Response<Void> handleIllegalParameter(Exception e) {
        return illegalParameter(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception e) {
        // Spring MVC 内置异常（404、405、415 等）保留原有 HTTP 状态码
        if (e instanceof ErrorResponse errorResponse) {
            return ResponseEntity.status(errorResponse.getStatusCode())
                    .body(Response.failure(ResponseCode.UN_ERROR.getCode(), errorResponse.getBody().getDetail()));
        }
        log.error("系统异常", e);
        return ResponseEntity.ok(Response.failure(ResponseCode.UN_ERROR));
    }

    private Response<Void> illegalParameter(String info) {
        log.warn("非法参数 {}", info);
        return Response.failure(ResponseCode.ILLEGAL_PARAMETER.getCode(), info);
    }

}
