package com.example.order_service.exception;

import com.example.order_service.dtos.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponse<Object>> handleRuntimeException(
            ApplicationException e
    ) {
        return ResponseEntity.status(e.getHttpStatus()).body(new BaseResponse<>(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Object>> handleRuntimeException(
            RuntimeException e
    ) {
        log.error("Exception ", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(500, null, "System error, plz try later"));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>>
    handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        List<FieldError> fieldErrors =
                e.getBindingResult().getFieldErrors();

        List<String> errorMessages = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {
            String errorMessage = fieldError.getField() + ": " + fieldError.getDefaultMessage();
            errorMessages.add(errorMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(400, null, errorMessages.toString()));
    }

}