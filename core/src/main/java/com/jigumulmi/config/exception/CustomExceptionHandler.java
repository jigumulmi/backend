package com.jigumulmi.config.exception;

import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.config.exception.errorCode.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j(topic = "ERROR_LOGGER")
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(ErrorResponseDto.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build()
            );
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(ErrorResponseDto.builder()
                .code(errorCode.name())
                .message(message)
                .build()
            );
    }

    private ResponseEntity<Object> handleExceptionInternal(MethodArgumentNotValidException e,
        ErrorCode errorCode) {
        List<ErrorResponseDto.ValidationError> validationErrorList = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(ErrorResponseDto.ValidationError::of)
            .collect(Collectors.toList());

        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(ErrorResponseDto.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(validationErrorList)
                .build()
            );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.error("handleIllegalArgument: " + e.getMessage());
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException: " + e.getMessage());
        ErrorCode errorCode = CommonErrorCode.RDB_INTEGRITY_VIOLATION;
        return handleExceptionInternal(errorCode, e.getMessage());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status,
        WebRequest request
    ) {
        log.error("handleMethodArgumentNotValid: " + e.getMessage());
        ErrorCode errorCode = CommonErrorCode.UNPROCESSABLE_ENTITY;
        return handleExceptionInternal(e, errorCode);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("handleCustomException: " + errorCode);

        String message = e.getMessage();
        if (message == null) {
            return handleExceptionInternal(errorCode);
        } else {
            return handleExceptionInternal(errorCode, message);
        }
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception e) {
        log.error("handleAllException: ", e);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode, e.toString());
    }
}
