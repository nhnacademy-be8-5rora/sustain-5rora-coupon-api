package store.aurora.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

//DTO 무결성 검증 오류시 오류 메시지 반환하는 역할
@RestControllerAdvice
public class CouponExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 유효성 검사 오류 메시지를 하나씩 가져와서 적절한 형식으로 반환
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())  // 필드와 메시지
                .collect(Collectors.joining(", "));  // 쉼표로 구분하여 메시지 결합
        return ResponseEntity.badRequest().body(message);
    }
}