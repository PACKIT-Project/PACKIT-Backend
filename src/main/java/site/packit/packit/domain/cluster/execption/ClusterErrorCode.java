package site.packit.packit.domain.cluster.execption;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.packit.packit.global.exception.ErrorCode;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ClusterErrorCode implements ErrorCode {

    CLUSTER_NOT_FOUND("CL-C-001", NOT_FOUND, "존재하지 않는 할 일 그룹입니다."),
    CLUSTER_NOT_EDIT("CL-C-002", NOT_FOUND, "사용자가 생성한 할 일 그룹이 아닙니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ClusterErrorCode(
            String code,
            HttpStatus httpStatus,
            String message
    ) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}