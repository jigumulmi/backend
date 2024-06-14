package com.jigumulmi.config.exception.errorCode;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape = OBJECT)
@AllArgsConstructor
@Getter
public enum PlaceErrorCode implements ErrorCode {
    DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "Maximum one review allowed"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

