package br.com.fiap.veiculo.infra.database.veiculoimpl;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.VeiculoEntity;
import br.com.fiap.veiculo.infra.database.repository.veiculo.VeiculoRepository;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class VeiculoGatewayImpl implements VeiculoPovider {

    private final VeiculoRepository veiculoRepository;

    public VeiculoGatewayImpl(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        VeiculoEntity entity = toEntity(veiculo);
        VeiculoEntity savedEntity = veiculoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Veiculo> buscarPorId(UUID id) {
        return veiculoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deletar(UUID id) {
        veiculoRepository.deleteById(id);
    }

    @Override
    public List<Veiculo> listarPorStatus(StatusVeiculo status) {
        return veiculoRepository.findByStatusOrderByPrecoAsc(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ==== Conversões ====
    private VeiculoEntity toEntity(Veiculo veiculo) {
        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(veiculo.getId());
        entity.setMarca(veiculo.getMarca());
        entity.setModelo(veiculo.getModelo());
        entity.setAno(veiculo.getAno());
        entity.setCor(veiculo.getCor());
        entity.setPreco(veiculo.getPreco());
        entity.setQuilometragem(veiculo.getQuilometragem());
        entity.setStatus(veiculo.getStatus());
        entity.setDataCadastro(veiculo.getDataCadastro());
        entity.setDataAtualizacao(veiculo.getDataAtualizacao());
        return entity;
    }

    private Veiculo toDomain(VeiculoEntity entity) {
        return Veiculo.builder()
                .id(entity.getId())
                .marca(entity.getMarca())
                .modelo(entity.getModelo())
                .ano(entity.getAno())
                .cor(entity.getCor())
                .preco(entity.getPreco())
                .quilometragem(entity.getQuilometragem())
                .status(entity.getStatus())
                .dataCadastro(entity.getDataCadastro())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }
}
