package com.ecommerce.webapp.exception;
import com.ecommerce.webapp.dto.response.Status;
import com.ecommerce.webapp.util.ShopConstants;
import com.ecommerce.webapp.util.StatusBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<Status> handleGlobalException(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Status.builder()
                .code(ShopConstants.ERR_CODE_INTERNAL_SERVER_ERROR)
                .message(ex.getMessage())
                .build());

    }

    // Add more exception handler methods for specific exception types as needed
    @ExceptionHandler(InvalidOrderStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Status> handleInvalidOrderStateException(InvalidOrderStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Status.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build());
    }

}
