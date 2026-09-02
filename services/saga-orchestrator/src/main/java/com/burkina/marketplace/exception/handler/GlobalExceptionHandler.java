package com.burkina.marketplace.exception.handler;

import com.burkina.common.response.ErrorResponse;
import com.burkina.marketplace.exception.*;
import com.burkina.marketplace.mapper.ErrorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler({
            EmptyCartException.class,
            ProductNotAvailableException.class,
            ReserveProductsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({
            CartNotFoundException.class,
            ProductNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            CartServiceUnavailableException.class,
            InventoryServiceUnavailableException.class,
            OrderServiceUnavailableException.class,
            PaymentServiceUnavailableException.class,
            ProductServiceUnavailableException.class
    })
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentRequired(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }

    @ExceptionHandler(SagaCompensationFailedException.class)
    public ResponseEntity<ErrorResponse> handleServerError(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
