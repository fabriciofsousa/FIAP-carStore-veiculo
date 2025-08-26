package br.com.fiap.veiculo.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.fiap.veiculo.exception.VeiculoNaoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ResponseEntity<String> handleVeiculoNaoEncontradoException(VeiculoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
