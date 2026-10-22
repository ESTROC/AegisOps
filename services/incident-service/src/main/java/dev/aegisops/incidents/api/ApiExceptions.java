package dev.aegisops.incidents.api;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiExceptions {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Map<String,Object>> invalid(MethodArgumentNotValidException ex){return ResponseEntity.badRequest().body(Map.of("error","Validation failed","details",ex.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).toList(),"time",Instant.now().toString()));}
 @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
 public ResponseEntity<Map<String,String>> conflict(){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","Ticket was modified by another user"));}
}
