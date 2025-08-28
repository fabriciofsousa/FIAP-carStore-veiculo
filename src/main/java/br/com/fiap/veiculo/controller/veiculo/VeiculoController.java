package br.com.fiap.veiculo.controller.veiculo;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.controller.veiculo.mapper.VeiculoMapper;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.usecase.veiculo.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/veiculo")
public class VeiculoController {

    private final CriarVeiculoUseCase criarVeiculoUseCase;
    private final ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase;
    private final AlterarVeiculoUseCase alterarVeiculoUseCase;
    private final DeletarVeiculoUseCase deletarVeiculoUseCase;
    private final ObterVeiculosPorStatusUseCase obterVeiculosPorStatusUseCase;

    public VeiculoController(
            CriarVeiculoUseCase criarVeiculoUseCase,
            ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase,
            AlterarVeiculoUseCase alterarVeiculoUseCase,
            DeletarVeiculoUseCase deletarVeiculoUseCase,
            ObterVeiculosPorStatusUseCase obterVeiculosPorStatusUseCase
    ) {
        this.criarVeiculoUseCase = criarVeiculoUseCase;
        this.obterVeiculoPorIdUseCase = obterVeiculoPorIdUseCase;
        this.alterarVeiculoUseCase = alterarVeiculoUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
        this.obterVeiculosPorStatusUseCase = obterVeiculosPorStatusUseCase;
    }

    @PostMapping
    public ResponseEntity<Veiculo> criarVeiculo(@RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
        Veiculo veiculo = VeiculoMapper.toDomain(veiculoRequestDTO);
        Veiculo criado = criarVeiculoUseCase.execute(veiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> obterVeiculoPorId(@PathVariable UUID id) {
        return obterVeiculoPorIdUseCase.execute(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Veiculo>> listarVeiculosDisponiveis() {
        List<Veiculo> veiculos = obterVeiculosPorStatusUseCase.execute(StatusVeiculo.DISPONIVEL);
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/vendidos")
    public ResponseEntity<List<Veiculo>> listarVeiculosVendidos() {
        List<Veiculo> veiculos = obterVeiculosPorStatusUseCase.execute(StatusVeiculo.VENDIDO);
        return ResponseEntity.ok(veiculos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizarVeiculo(@PathVariable UUID id, @RequestBody @Valid Veiculo veiculo) {
        Veiculo atualizado = alterarVeiculoUseCase.execute(id, veiculo);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable UUID id) {
        deletarVeiculoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}