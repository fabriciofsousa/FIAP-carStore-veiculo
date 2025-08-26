package br.com.fiap.veiculo.exception;

public class VeiculoNaoEncontradoException extends RuntimeException{
    public VeiculoNaoEncontradoException(String message) {
        super(message);
    }
}
