package br.com.fiap.veiculo.controller.veiculo.mapper;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestUpdateDTO;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;

import java.time.LocalDateTime;

public class VeiculoMapper {
    public static Veiculo toDomain(VeiculoRequestDTO dto) {
        return Veiculo.builder()
                .id(null)
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .ano(dto.getAno())
                .cor(dto.getCor())
                .preco(dto.getPreco())
                .quilometragem(dto.getQuilometragem())
                .status(StatusVeiculo.DISPONIVEL)
                .dataCadastro(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

    }
    public static Veiculo toDomain(VeiculoRequestUpdateDTO dto) {
        return Veiculo.builder()
                .id(null)
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .ano(dto.getAno())
                .cor(dto.getCor())
                .preco(dto.getPreco())
                .quilometragem(dto.getQuilometragem())
                .status(dto.getStatus())
                .dataCadastro(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }
}
