package br.com.fiap.veiculo.controller.veiculo;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.controller.veiculo.mapper.VeiculoMapper;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.usecase.veiculo.AlterarVeiculoUseCase;
import br.com.fiap.veiculo.usecase.veiculo.CriarVeiculoUseCase;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculoPorIdUseCase;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculosPorStatusUseCase;
import br.com.fiap.veiculo.usecase.veiculo.DeletarVeiculoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {
    private final CriarVeiculoUseCase criarVeiculoUseCase;
    private final ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase;
    private final AlterarVeiculoUseCase alterarVeiculoUseCase;
    private final DeletarVeiculoUseCase deletarVeiculo;
    private final ObterVeiculosPorStatusUseCase obterVeiculosPorStatusUseCase;

    public VeiculoController(
            CriarVeiculoUseCase criarVeiculoUseCase,
            ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase,
            AlterarVeiculoUseCase alterarVeiculoUseCase,
            DeletarVeiculoUseCase deletarVeiculo,
            ObterVeiculosPorStatusUseCase obterVeiculosPorStatusUseCase
    ) {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
        this.obterVeiculoPorIdUseCase = obterVeiculoPorIdUseCase;
        this.alterarVeiculoUseCase = alterarVeiculoUseCase;
        this.deletarVeiculo = deletarVeiculo;
        this.obterVeiculosPorStatusUseCase = obterVeiculosPorStatusUseCase;
    }

    // Cadastrar veículo
    @PostMapping
    public ResponseEntity<Veiculo> criarVeiculo(@RequestBody VeiculoRequestDTO veiculoRequestDTO) {
        var veiculo = VeiculoMapper.toDomain(veiculoRequestDTO);
        return ResponseEntity.ok(criarVeiculoUseCase.execute(veiculo));
    }

    // Buscar veículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Veiculo>> obterVeiculoPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(obterVeiculoPorIdUseCase.execute(id));
    }

    // Listar veículos disponíveis à venda (ordenados por preço)
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Veiculo>> listarVeiculosDisponiveis() {
        return ResponseEntity.ok(obterVeiculosPorStatusUseCase.execute(StatusVeiculo.DISPONIVEL));
    }

    // Listar veículos vendidos (ordenados por preço)
    @GetMapping("/vendidos")
    public ResponseEntity<List<Veiculo>> listarVeiculosVendidos() {
        return ResponseEntity.ok(obterVeiculosPorStatusUseCase.execute(StatusVeiculo.VENDIDO));
    }

    // Atualizar veículo
    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> updateVeiculo(@PathVariable UUID id, @RequestBody Veiculo veiculo) {
        return ResponseEntity.ok(alterarVeiculoUseCase.execute(id, veiculo));
    }

    // Deletar veículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable UUID id) {
        deletarVeiculo.execute(id);
        return ResponseEntity.noContent().build();
    }
}
