package com.ssafy.yogiattacku.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_AUTHENTICATED(UNAUTHORIZED, 1000, "로그인이 필요한 요청입니다."),
    ACCESS_TOKEN_NOT_FOUND(UNAUTHORIZED, 1001, "Access Token이 존재하지 않습니다."),
    ACCESS_TOKEN_INVALID(UNAUTHORIZED, 1002, "유효하지 않은 Access Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(UNAUTHORIZED, 1003, "Refresh Token이 존재하지 않습니다."),
    REFRESH_TOKEN_INVALID(UNAUTHORIZED, 1004, "유효하지 않은 Refresh Token입니다."),
    ACCESS_DENIED(FORBIDDEN, 1005, "접근 권한이 없습니다."),

    ATTRACTION_VECTOR_SEARCH_ERROR(NOT_FOUND, 2000, "유사 관광지 조회 중 오류가 발생했습니다."),
    EMBEDDING_REQUEST_FAILED(INTERNAL_SERVER_ERROR, 2001, "OpenAI 임베딩 요청 중 오류가 발생했습니다."),
    RECOMMENDATION_GENERATION_FAILED(INTERNAL_SERVER_ERROR, 2002, "여행 추천 생성 중 오류가 발생했습니다."),

    DEMO_ERROR(HttpStatus.BAD_REQUEST, 9999, "데모 에러입니다."),
    ;
    private final HttpStatus status;
    private final int code;
    private final String message;
}
