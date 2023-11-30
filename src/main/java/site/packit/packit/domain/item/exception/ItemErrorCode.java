package site.packit.packit.domain.item.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum ItemErrorCode implements ErrorCode {

    ITEM_NOT_FOUND("IT-C-001", NOT_FOUND, "존재하지 않는 체크리스트입니다."),
    ITEM_NOT_EDIT("IT-C-002", NOT_FOUND, "사용자가 편집 가능한 아이템이 아닙니다."),
    ITEM_NOT_DELETE("IT-C-003", NOT_FOUND, "사용자가 삭제 가능한 아이템이 아닙니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ItemErrorCode(
            String code,
            HttpStatus httpStatus,
            String message
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

