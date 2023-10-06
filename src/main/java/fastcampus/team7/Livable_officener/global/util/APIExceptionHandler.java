package fastcampus.team7.Livable_officener.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class APIExceptionHandler {

    public static ResponseEntity<?> handleExceptionInternal(String errorMessage, HttpStatus httpStatus) {
        return APIErrorResponse.of(httpStatus, errorMessage);
    }

    public static ResponseEntity<?> handleExceptionInternal(Exception e, HttpStatus httpStatus) {
        return APIErrorResponse.of(httpStatus, e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> httpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException e) {
        return handleExceptionInternal("지원하지 않는 HttpMethod 요청입니다.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler
    public ResponseEntity<?> methodArgumentTypeMismatchExceptionHandle(MethodArgumentTypeMismatchException e) {
        return handleExceptionInternal("PathVariable 타입이 올바르지 않습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return handleExceptionInternal(e.getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> noHandlerFoundExceptionHandle(NoHandlerFoundException e) {
        return handleExceptionInternal("API가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> httpMessageNotReadableExceptionHandle(HttpMessageNotReadableException e) {
        e.printStackTrace();
        return handleExceptionInternal("RequestBody 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> bindExceptionHandle(BindException e) {
        return handleExceptionInternal("요청 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler
//    public ResponseEntity<?> handleIOException(IOException e) {
//        return handleExceptionInternal(e, "IOException", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException e) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler
//    public ResponseEntity<?> handleException(Exception e) {
//        return handleExceptionInternal(e, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
