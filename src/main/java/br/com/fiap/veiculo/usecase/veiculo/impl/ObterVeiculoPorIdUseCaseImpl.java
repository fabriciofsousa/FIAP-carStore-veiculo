package br.com.fiap.veiculo.usecase.veiculo.impl;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculoPorIdUseCase;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ObterVeiculoPorIdUseCaseImpl implements ObterVeiculoPorIdUseCase {

    private final VeiculoPovider veiculoGateway;

    public ObterVeiculoPorIdUseCaseImpl(VeiculoPovider veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public Optional<Veiculo> execute(UUID id) {
        validarId(id);

        Optional<Veiculo> veiculoOpt = veiculoGateway.buscarPorId(id);

        if (veiculoOpt.isEmpty()) {
            throw new VeiculoNaoEncontradoException("Veículo não encontrado para o ID: " + id);
        }

        return veiculoOpt;
    }

    private void validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do veículo não pode ser nulo");
        }
    }
}
