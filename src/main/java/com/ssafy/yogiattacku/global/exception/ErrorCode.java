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
    USER_NOT_FOUND(NOT_FOUND, 1006, "사용자를 찾을 수 없습니다."),

    ATTRACTION_VECTOR_SEARCH_ERROR(NOT_FOUND, 2000, "유사 관광지 조회 중 오류가 발생했습니다."),
    EMBEDDING_REQUEST_FAILED(INTERNAL_SERVER_ERROR, 2001, "OpenAI 임베딩 요청 중 오류가 발생했습니다."),
    RECOMMENDATION_GENERATION_FAILED(INTERNAL_SERVER_ERROR, 2002, "여행 추천 생성 중 오류가 발생했습니다."),

    S3_OBJECT_NOT_FOUND(NOT_FOUND, 3000, "S3 객체를 찾을 수 없습니다."),
    S3_DELETE_FAILED(INTERNAL_SERVER_ERROR, 3002, "S3 삭제 중 오류가 발생했습니다."),
    FILE_SIZE_EXCEEDED(BAD_REQUEST, 3004, "업로드 가능한 파일 크기를 초과했습니다."),

    INVALID_REQUEST(BAD_REQUEST, 4000, "요청 값이 올바르지 않습니다."),
    CATEGORY_NOT_FOUND(NOT_FOUND, 4001, "존재하지 않는 카테고리가 포함되어 있습니다."),
    S3_KEY_INVALID(BAD_REQUEST, 4002, "bucketRootKey에 속하지 않는 s3Key가 포함되어 있습니다."),
    EMPTY_S3_KEYS(BAD_REQUEST, 4003, "s3Keys는 비어 있을 수 없습니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, 4004, "게시글을 찾을 수 없습니다."),

    DEMO_ERROR(HttpStatus.BAD_REQUEST, 9999, "데모 에러입니다."),
    ;
    private final HttpStatus status;
    private final int code;
    private final String message;
}
