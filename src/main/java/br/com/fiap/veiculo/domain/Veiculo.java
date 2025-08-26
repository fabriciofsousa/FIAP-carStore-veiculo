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

    @NotBlank(message = "Marca não pode ser vazio")
    private String marca;

    @NotBlank(message = "Modelo não pode ser vazio")
    private String modelo;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano deve ser maior ou igual a 1900")
    @Max(value = 2100, message = "Ano não pode ser maior que o ano atual")
    private Integer ano;

    @NotBlank(message = "Cor não pode ser vazio")
    private String cor;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    private BigDecimal preco;

    @NotNull(message = "Quilometragem é obrigatória")
    @PositiveOrZero(message = "Quilometragem não pode ser negativa")
    private Integer quilometragem;

    @Builder.Default
    private StatusVeiculo status = StatusVeiculo.DISPONIVEL;

    @Builder.Default
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();
}
