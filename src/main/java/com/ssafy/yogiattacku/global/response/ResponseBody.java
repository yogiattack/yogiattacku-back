package com.ssafy.yogiattacku.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseBody<T> {
    private final int statusCode;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer errorCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseBody(T data) {
        this.statusCode = HttpStatus.OK.value();
        this.message = HttpStatus.OK.getReasonPhrase();
        this.data = data;
    }

    public ResponseBody(ErrorCode errorCode) {
        this.statusCode = errorCode.getStatus().value();
        this.errorCode = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public static <T> ResponseBody<T> success(T data) {
        return new ResponseBody<>(data);
    }

    public static <T> ResponseBody<T> error(ErrorCode errorCode) {
        return new ResponseBody<>(errorCode);
    }
}
