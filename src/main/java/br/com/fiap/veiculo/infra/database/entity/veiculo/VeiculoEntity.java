package br.com.fiap.veiculo.infra.database.entity.veiculo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "veiculo")
public class VeiculoEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_veiculo")
    private UUID id;

    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;

    private BigDecimal preco;
    private Integer quilometragem;

    @Enumerated(EnumType.STRING)
    private StatusVeiculo status;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;


}
