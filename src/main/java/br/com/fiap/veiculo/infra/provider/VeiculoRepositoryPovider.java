package br.com.fiap.veiculo.infra.provider;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepositoryPovider {
    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarPorId(UUID id);

    List<Veiculo> listarTodos();

    void deletar(UUID id);

    List<Veiculo> listarPorStatus(StatusVeiculo status);

}
