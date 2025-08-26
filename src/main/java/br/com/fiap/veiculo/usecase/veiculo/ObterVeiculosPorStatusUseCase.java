package br.com.fiap.veiculo.usecase.veiculo;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;

import java.util.List;

public interface ObterVeiculosPorStatusUseCase {

    List<Veiculo> execute(StatusVeiculo status);

}
