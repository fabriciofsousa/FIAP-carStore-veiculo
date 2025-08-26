package br.com.fiap.veiculo.usecase.veiculo;

import br.com.fiap.veiculo.domain.Veiculo;

import java.util.UUID;

public interface AlterarVeiculoUseCase {
    public Veiculo execute(UUID id, Veiculo veiculo);

}
