package br.com.fiap.veiculo.domain;

import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {
    private UUID id;

    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private BigDecimal preco;
    private Integer quilometragem;
    @Builder.Default
    private StatusVeiculo status = StatusVeiculo.DISPONIVEL;

    @Builder.Default
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
}
