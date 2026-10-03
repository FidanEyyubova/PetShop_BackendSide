package com.petshop.backend.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard envelope for every API response")
public class ApiResponse<T> {

    @Schema(description = "true when the request succeeded", example = "true")
    private Boolean success;

    @Schema(description = "Human-readable message", example = "Products fetched successfully")
    private String message;

    @Schema(description = "Payload; on validation errors a map of field -> message")
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Machine-readable error code, present only on errors", example = "OUT_OF_STOCK")
    private String code;

    public ApiResponse() {
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(Boolean success, String message, T data, String code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(false, message, data, code);
    }

    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<T>();
    }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public static class ApiResponseBuilder<T> {
        private Boolean success;
        private String message;
        private T data;
        private String code;

        ApiResponseBuilder() {
        }

        public ApiResponseBuilder<T> success(Boolean success) { this.success = success; return this; }
        public ApiResponseBuilder<T> message(String message) { this.message = message; return this; }
        public ApiResponseBuilder<T> data(T data) { this.data = data; return this; }
        public ApiResponseBuilder<T> code(String code) { this.code = code; return this; }

        public ApiResponse<T> build() {
            return new ApiResponse<T>(success, message, data, code);
        }
    }
}
