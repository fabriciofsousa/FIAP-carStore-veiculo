package br.com.fiap.veiculo.usecase.veiculo.impl;

import br.com.fiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.fiap.veiculo.exception.VeiculoVendidoException;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.DeletarVeiculoUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletarVeiculoUseCaseImpl implements DeletarVeiculoUseCase {

    private final VeiculoPovider veiculoGateway;

    public DeletarVeiculoUseCaseImpl(VeiculoPovider veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public void execute(UUID id) {
        validarId(id);

        var veiculo = veiculoGateway.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado"));

         if (veiculo.getStatus() == StatusVeiculo.VENDIDO) {
             throw new VeiculoVendidoException("Não é possível deletar um veículo vendido");
         }

        veiculoGateway.deletar(id);
    }

    private void validarId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do veículo não pode ser nulo");
        }
    }
}
