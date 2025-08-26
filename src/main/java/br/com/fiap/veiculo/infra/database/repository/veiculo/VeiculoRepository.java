package br.com.fiap.veiculo.infra.database.repository.veiculo;

import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<VeiculoEntity, UUID> {

    List<VeiculoEntity> findByStatusOrderByPrecoAsc(StatusVeiculo status);

}
