package com.example.crm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi Validation (@Valid trên DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse res = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_FAILED",
            "Dữ liệu đầu vào không hợp lệ",
            LocalDateTime.now(),
            fieldErrors
        );
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    // 2. Bắt lỗi Không tìm thấy bản ghi (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse res = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    // 3. Bắt lỗi Nghiệp vụ (409)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        ErrorResponse res = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "BUSINESS_RULE_VIOLATION",
            ex.getMessage(),
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    // 4. Bắt lỗi trùng khóa UNIQUE trong MySQL (Ví dụ: trùng Email khách hàng)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKey(DuplicateKeyException ex) {
        ErrorResponse res = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "DUPLICATE_DATA",
            "Dữ liệu bị trùng lặp trong hệ thống (Email hoặc Mã sự kiện đã tồn tại).",
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    // 5. Bắt mọi lỗi hệ thống khác (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Lỗi hệ thống chưa xác định tại CRM: ", ex);
        ErrorResponse res = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "Đã xảy ra lỗi nội bộ hệ thống.",
            LocalDateTime.now(),
            null
        );
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}