package br.com.fiap.veiculo.usecase.veiculo.impl;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculoUseCase;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ObterVeiculoUseCaseImpl implements ObterVeiculoUseCase {

    private final VeiculoPovider veiculoGateway;

    public ObterVeiculoUseCaseImpl(VeiculoPovider veiculoGateway) {
        if (veiculoGateway == null) {
            throw new IllegalArgumentException("VeiculoGateway não pode ser nulo");
        }
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public List<Veiculo> execute() {
        List<Veiculo> veiculos = veiculoGateway.listarTodos();
        return veiculos != null ? veiculos : Collections.emptyList();
    }
}
