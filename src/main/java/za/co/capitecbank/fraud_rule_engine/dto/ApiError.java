package za.co.capitecbank.fraud_rule_engine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {
    public record FieldError(
            String field,
            String message,
            Object rejectedValue
    ) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                null
        );
    }

    public static ApiError withFieldErrors(int status, String error, String message,
                                           String path, List<FieldError> fieldErrors) {
        return new ApiError(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                fieldErrors
        );
    }
}
