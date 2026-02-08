package com.healnexus.exception;

import com.healnexus.dto.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> validationException(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err->err.getField()+" : "+err.getDefaultMessage())
                .findFirst().orElse(
                     "validation failed"
                );
        APIResponse apiResponse=new APIResponse(message,false);

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> resourceNotFoundException(ResourceNotFoundException ex){
        String message=ex.getMessage();
        APIResponse apiResponse=new APIResponse(message,false);
        return  new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse> illegalArgumentException(IllegalArgumentException ex){
        String message=ex.getMessage();
        APIResponse apiResponse=new APIResponse(message,false);
        return  new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> genericException(Exception ex){

        APIResponse apiResponse=new APIResponse( "Something went wrong. Please try again later.",false);
        return  new ResponseEntity<>(apiResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
