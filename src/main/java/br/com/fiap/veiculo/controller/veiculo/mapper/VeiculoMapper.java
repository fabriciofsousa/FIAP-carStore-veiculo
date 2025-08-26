package br.com.fiap.veiculo.controller.veiculo.mapper;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;

import java.time.LocalDateTime;

public class VeiculoMapper {
    public static Veiculo toDomain(VeiculoRequestDTO dto) {
        return Veiculo.builder()
                .id(null) // gerado pelo banco
                .marca(dto.marca())
                .modelo(dto.modelo())
                .ano(dto.ano())
                .cor(dto.cor())
                .preco(dto.preco())
                .quilometragem(dto.quilometragem())
                .status(StatusVeiculo.DISPONIVEL) // default no cadastro
                .dataCadastro(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }}
