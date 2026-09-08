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
            ReservationFailedException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            CartServiceException.class,
            InventoryServiceException.class,
            OrderServiceException.class,
            PaymentServiceException.class,
            ProductServiceException.class
    })
    public ResponseEntity<ErrorResponse> handleServiceError(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentRequired(RuntimeException e) {
        ErrorResponse response = errorMapper.from(e);

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }
}
