package pu.fmi.webprogramming.contoller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pu.fmi.webprogramming.exception.DeliveryCustomException;
import pu.fmi.webprogramming.exception.ErrorDTO;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(DeliveryCustomException.class)
    public ResponseEntity<ErrorDTO> handleDeliveryCustomException(DeliveryCustomException ex) {
        ErrorDTO error =  new ErrorDTO(404, ex.getMessage(),"");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
