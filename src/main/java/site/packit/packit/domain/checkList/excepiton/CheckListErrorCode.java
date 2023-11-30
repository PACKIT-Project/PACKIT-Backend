package site.packit.packit.domain.checkList.excepiton;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum CheckListErrorCode implements ErrorCode {

    CHECKLIST_NOT_FOUND("CL-C-001", NOT_FOUND, "존재하지 않는 체크리스트입니다."),
    CHECKLIST_NOT_DELETE("CL-C-002", NOT_FOUND, "사용자가 생성한 체크리스트가 아닙니다."),
    CHECKLIST_NOT_EDIT("CL-C-003", NOT_FOUND, "사용자가 편집 가능한 체크리스트가 아닙니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    CheckListErrorCode(
            String code,
            HttpStatus httpStatus,
            String message
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

