package com.jigumulmi.config.exception.errorCode;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape = OBJECT)
@AllArgsConstructor
@Getter
public enum AdminErrorCode implements ErrorCode {
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

