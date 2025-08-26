package br.com.fiap.veiculo.controller.veiculo.dto;

import java.math.BigDecimal;

public record VeiculoRequestDTO(
        String marca,
        String modelo,
        Integer ano,
        String cor,
        BigDecimal preco,
        Integer quilometragem
) {
}
