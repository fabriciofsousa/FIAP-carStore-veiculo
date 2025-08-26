package br.com.fiap.veiculo.usecase.veiculo.impl;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculosPorStatusUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObterVeiculosPorStatusUseCaseImpl implements ObterVeiculosPorStatusUseCase {

    private final VeiculoPovider veiculoGateway;

    public ObterVeiculosPorStatusUseCaseImpl(VeiculoPovider veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public List<Veiculo> execute(StatusVeiculo status) {
        validarStatus(status);
        return veiculoGateway.listarPorStatus(status);
    }

    private void validarStatus(StatusVeiculo status) {
        if (status == null) {
            throw new IllegalArgumentException("Status do veículo não pode ser nulo");
        }

        boolean valido = status == StatusVeiculo.DISPONIVEL
                || status == StatusVeiculo.RESERVADO
                || status == StatusVeiculo.VENDIDO;

        if (!valido) {
            throw new IllegalArgumentException("Status do veículo inválido: " + status);
        }
    }
}
