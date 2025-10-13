package br.com.fiap.veiculo.usecase.veiculo.impl;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.AlterarVeiculoUseCase;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AlterarVeiculoUseCaseImpl implements AlterarVeiculoUseCase {

    private final VeiculoPovider veiculoPovider;

    public AlterarVeiculoUseCaseImpl(VeiculoPovider veiculoPovider) {
        this.veiculoPovider = veiculoPovider;
    }

    @Override
    public Veiculo execute(UUID id, Veiculo veiculoPatch) {
        if (id == null) {
            throw new IllegalArgumentException("ID do veículo não pode ser nulo");
        }

        Veiculo veiculoExistente = veiculoPovider.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado"));

        if (veiculoPatch.getMarca() != null) {
            validarStringNaoVazia("marca", veiculoPatch.getMarca());
            veiculoExistente.setMarca(veiculoPatch.getMarca());
        }
        if (veiculoPatch.getModelo() != null) {
            validarStringNaoVazia("modelo", veiculoPatch.getModelo());
            veiculoExistente.setModelo(veiculoPatch.getModelo());
        }
        if (veiculoPatch.getCor() != null) {
            validarStringNaoVazia("cor", veiculoPatch.getCor());
            veiculoExistente.setCor(veiculoPatch.getCor());
        }
        if (veiculoPatch.getAno() != null) {
            validarAno(veiculoPatch.getAno());
            veiculoExistente.setAno(veiculoPatch.getAno());
        }
        if (veiculoPatch.getPreco() != null) {
            validarPreco(veiculoPatch.getPreco());
            veiculoExistente.setPreco(veiculoPatch.getPreco());
        }
        if (veiculoPatch.getQuilometragem() != null) {
            validarQuilometragem(veiculoPatch.getQuilometragem());
            veiculoExistente.setQuilometragem(veiculoPatch.getQuilometragem());
        }
        if (veiculoPatch.getStatus() != null) {
            validarStatus(veiculoPatch.getStatus());
            veiculoExistente.setStatus(StatusVeiculo.valueOf(String.valueOf(veiculoPatch.getStatus())));
        }

        veiculoExistente.setDataAtualizacao(LocalDateTime.now());

        return veiculoPovider.salvar(veiculoExistente);
    }

    private void validarStringNaoVazia(String campo, String valor) {
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + campo + "' não pode ser vazio");
        }
    }

    private void validarAno(Integer ano) {
        int anoAtual = LocalDate.now().getYear();
        if (ano < 1900 || ano > anoAtual) {
            throw new IllegalArgumentException("Ano inválido: " + ano);
        }
    }

    private void validarPreco(java.math.BigDecimal preco) {
        if (preco.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
    }

    private void validarQuilometragem(Integer km) {
        if (km < 0) {
            throw new IllegalArgumentException("Quilometragem não pode ser negativa");
        }
    }

    private void validarStatus(StatusVeiculo status) {
        if (status == null) {
            throw new IllegalArgumentException("Status do veículo inválido");
        }
    }
}

