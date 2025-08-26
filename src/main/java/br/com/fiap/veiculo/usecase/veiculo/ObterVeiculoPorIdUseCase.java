package br.com.fiap.veiculo.usecase.veiculo;

import br.com.fiap.veiculo.domain.Veiculo;

import java.util.Optional;
import java.util.UUID;

public interface ObterVeiculoPorIdUseCase {
    Optional<Veiculo> execute(UUID id);
}
