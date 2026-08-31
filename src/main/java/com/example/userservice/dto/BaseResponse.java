package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "meta",
        "data"
})
public class BaseResponse<T> {

    private T data;
    private Metadata meta = new Metadata();
    public BaseResponse() {
    }
    public BaseResponse(int code, String message) {
        this.meta.code = code;
        this.meta.message = message;
    }
    public BaseResponse(T data, String message) {
        this.data = data;
        this.meta.message = message;
    }
    public BaseResponse(int code, T data, String message) {
        this.data = data;
        this.meta.code = code;
        this.meta.message = message;
    }


    public static <T> BaseResponse<T> ofSuccess(T data) {
        BaseResponse<T> response = new BaseResponse<>();

        response.data = data;
        response.meta.code = HttpStatus.OK.value();

        return response;
    }

    public static <T> BaseResponse<List<T>> ofSuccess(Page<T> page) {
        BaseResponse<List<T>> response = new BaseResponse<>();

        response.data = page.getContent();
        response.meta.page = page.getNumber();
        response.meta.size = page.getSize();
        response.meta.total = page.getTotalElements();
        response.meta.code = HttpStatus.OK.value();

        return response;
    }

    public static <T> BaseResponse<T> ofDeleteSuccess() {
        BaseResponse<T> response = new BaseResponse<>();

        response.meta.code = HttpStatus.NO_CONTENT.value();

        return response;
    }

    public static <T> BaseResponse<T> ofSuccessMessage(String message) {
        BaseResponse<T> response = new BaseResponse<>();

        response.meta.message = message;
        response.meta.code = HttpStatus.OK.value();

        return response;
    }

    public static BaseResponse<String> ofSuccessDataMessage(String message) {
        BaseResponse<String> response = new BaseResponse<>();

        response.data = message;
        response.meta.code = HttpStatus.OK.value();

        return response;
    }

    public static BaseResponse<Void> ofError(
            HttpStatus status,
            String message,
            List<FieldViolation> errors
    ) {
        BaseResponse<Void> response = new BaseResponse<>();

        response.meta.code = status.value();
        response.meta.message = message;
        response.meta.errors = errors;

        return response;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Metadata {

        private int code;
        private Integer page;
        private Integer size;
        private Long total;
        private List<FieldViolation> errors;
        private String message;
        private String requestId;
    }
}
