package ifive.idrop.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TOKEN_NOT_EXIST(HttpStatus.BAD_REQUEST, 1, "Access Token이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, 2, "잘못된 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 3, "Access Token이 만료되었습니다."),
    UNAUTHORIZED_USER(HttpStatus.BAD_REQUEST, 4, "접근 권한이 없는 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 5, "회원 조회 시 해당 회원을 찾을 수 없습니다."),
    INVALID_ROLE_OF_USER(HttpStatus.BAD_REQUEST, 6, "회원의 역할이 정확하지 않습니다."),
    DUPLICATE_USERID(HttpStatus.BAD_REQUEST, 7, "이미 존재하는 아이디입니다."),
    USERID_NOT_EXIST(HttpStatus.BAD_REQUEST, 8, "존재하지 않는 아이디입니다."),
    DRIVER_NOT_EXIST(HttpStatus.BAD_REQUEST, 9, "존재하지 않는 기사입니다."),
    CHILD_NOT_EXIST(HttpStatus.BAD_REQUEST, 10, "등록되지 않은 아이입니다."),
    ALL_CHILD_NOT_EXIST(HttpStatus.BAD_REQUEST, 11, "아이 정보가 없습니다"),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, 12, "비밀번호가 맞지 않습니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, 13, "성별이 정확하지 않습니다."),
    INVALID_DAY_OF_WEEK(HttpStatus.BAD_REQUEST, 14, "요일이 정확하지 않습니다."),
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, 15, "구독이 존재하지 않습니다."),
    INVALID_PICKUP_STATUS(HttpStatus.BAD_REQUEST, 16, "픽업 상태가 정확하지 않습니다."),
    CURRENT_PICKUP_NOT_FOUND(HttpStatus.NOT_FOUND, 17, "현재 업무 시간인 픽업이 없습니다."),
    PICKUP_NOT_FOUND(HttpStatus.NOT_FOUND, 18, "픽업이 없습니다."),
    PICKUP_INFO_NOT_EXIST(HttpStatus.BAD_REQUEST, 19, "해당하는 픽업 정보가 없습니다."),
    PICKUP_ALREADY_END(HttpStatus.BAD_REQUEST, 20, "이미 종료된 픽업입니다."),
    IMAGE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 21, "이미지 업로드 중 문제가 발생했습니다."),
    DIRECTION_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, 22, "경로가 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
