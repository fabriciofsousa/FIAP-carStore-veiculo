package br.com.fiap.veiculo.controller.veiculo;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestUpdateDTO;
import br.com.fiap.veiculo.controller.veiculo.mapper.VeiculoMapper;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.usecase.veiculo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "Cria um novo veículo",
            description = "Cadastra um veículo no sistema",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do veículo (exemplo)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VeiculoRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "exemploCriarVeiculo",
                                            value = """
                                                    {
                                                      "marca": "Honda",
                                                      "modelo": "Civic",
                                                      "ano": 2022,
                                                      "cor": "Preto",
                                                      "preco": 95000.0,
                                                      "quilometragem": 15000
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
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

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Veiculo>> listarVeiculosPorStatus(
            @Parameter(
                    description = "Status do veículo",
                    required = true,
                    schema = @Schema(implementation = StatusVeiculo.class),
                    example = "DISPONIVEL"
            )
            @PathVariable StatusVeiculo status
    ) {
        List<Veiculo> veiculos = obterVeiculosPorStatusUseCase.execute(status);
        return ResponseEntity.ok(veiculos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizarVeiculo(@PathVariable UUID id, @RequestBody @Valid VeiculoRequestUpdateDTO veiculoRequestDTO) {
        Veiculo veiculo = VeiculoMapper.toDomain(veiculoRequestDTO);
        Veiculo atualizado = alterarVeiculoUseCase.execute(id, veiculo);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable UUID id) {
        deletarVeiculoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}