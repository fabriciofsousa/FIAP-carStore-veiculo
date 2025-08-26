package br.com.fiap.veiculo.usecase.veiculo.impl;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.CriarVeiculoUseCase;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CriarVeiculoUseCaseImpl implements CriarVeiculoUseCase {

    private final VeiculoPovider veiculoGateway;

    public CriarVeiculoUseCaseImpl(VeiculoPovider veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    @Override
    public Veiculo execute(Veiculo veiculo) {
        validarVeiculo(veiculo);

        if (veiculo.getStatus() == null) {
            veiculo.setStatus(StatusVeiculo.DISPONIVEL);
        }

        return veiculoGateway.salvar(veiculo);
    }

    private void validarVeiculo(Veiculo veiculo) {
        validarStringNaoVazia("marca", veiculo.getMarca());
        validarStringNaoVazia("modelo", veiculo.getModelo());
        validarStringNaoVazia("cor", veiculo.getCor());
        validarAno(veiculo.getAno());
        validarPreco(veiculo.getPreco());
        validarQuilometragem(veiculo.getQuilometragem());
        validarStatus(veiculo.getStatus());
    }

    private void validarStringNaoVazia(String campo, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo '" + campo + "' não pode ser vazio");
        }
    }

    private void validarAno(Integer ano) {
        if (ano == null) {
            throw new IllegalArgumentException("O campo 'ano' não pode ser nulo");
        }
        int anoAtual = LocalDate.now().getYear();
        if (ano < 1900 || ano > anoAtual) {
            throw new IllegalArgumentException("Ano inválido: " + ano);
        }
    }

    private void validarPreco(java.math.BigDecimal preco) {
        if (preco == null || preco.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser nulo ou negativo");
        }
    }

    private void validarQuilometragem(Integer km) {
        if (km == null || km < 0) {
            throw new IllegalArgumentException("Quilometragem não pode ser nula ou negativa");
        }
    }

    private void validarStatus(StatusVeiculo status) {
        if (status != null && !(status == StatusVeiculo.DISPONIVEL
                || status == StatusVeiculo.RESERVADO
                || status == StatusVeiculo.VENDIDO)) {
            throw new IllegalArgumentException("Status do veículo inválido");
        }
    }
}
