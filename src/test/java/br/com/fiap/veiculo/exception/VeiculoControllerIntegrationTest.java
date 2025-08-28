package br.com.fiap.veiculo.exception;

import br.com.fiap.veiculo.config.GlobalExceptionHandler;
import br.com.fiap.veiculo.controller.veiculo.VeiculoController;
import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.usecase.veiculo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VeiculoController.class)
@Import(GlobalExceptionHandler.class)
class VeiculoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // todos os usecases que o controller injeta como dependência precisam ser MockBean aqui
    @MockBean private CriarVeiculoUseCase criarVeiculoUseCase;
    @MockBean private ObterVeiculoUseCase obterVeiculoUseCase;
    @MockBean private ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase;
    @MockBean private AlterarVeiculoUseCase alterarVeiculoUseCase;
    @MockBean private DeletarVeiculoUseCase deletarVeiculo;
    @MockBean private ObterVeiculosPorStatusUseCase obterVeiculosPorStatusUseCase; // <- adicionado

    // ----- POST /veiculo -----
    @Test
    void post_quandoValido_entao201_comVeiculoCriado() throws Exception {
        UUID id = UUID.randomUUID();
        Veiculo criado = Veiculo.builder()
                .id(id)
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2020)
                .cor("Prata")
                .preco(new BigDecimal("95000"))
                .quilometragem(15000)
                .status(StatusVeiculo.DISPONIVEL)
                .build();

        Mockito.when(criarVeiculoUseCase.execute(any(Veiculo.class))).thenReturn(criado);

        VeiculoRequestDTO dto = VeiculoRequestDTO.builder()
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2020)
                .cor("Prata")
                .preco(new BigDecimal("95000"))
                .quilometragem(15000)
                .build();

        mockMvc.perform(post("/veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"))
                .andExpect(jsonPath("$.status").value(StatusVeiculo.DISPONIVEL.name()));
    }

    @Test
    void post_quandoRuntimeException_entao400_comMensagemDoHandler() throws Exception {
        Mockito.when(criarVeiculoUseCase.execute(any(Veiculo.class)))
                .thenThrow(new RuntimeException("Erro ao criar"));

        VeiculoRequestDTO dto = VeiculoRequestDTO.builder()
                .marca("X")
                .modelo("Y")
                .ano(2000)
                .cor("Azul")
                .preco(new BigDecimal("1"))
                .quilometragem(0)
                .build();

        mockMvc.perform(post("/veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao criar"));
    }

    @Test
    void post_quandoInvalido_entao400_porValidacao() throws Exception {
        VeiculoRequestDTO invalid = VeiculoRequestDTO.builder()
                .marca("") // not blank
                .modelo("") // not blank
                .ano(1800) // < 1900
                .cor("") // not blank
                .preco(new BigDecimal("-1")) // negative
                .quilometragem(-10) // negative
                .build();

        mockMvc.perform(post("/veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ----- GET /veiculo/{id} -----
    @Test
    void getById_quandoExiste_entao200_comVeiculo() throws Exception {
        UUID id = UUID.randomUUID();
        Veiculo v = Veiculo.builder()
                .id(id)
                .marca("VW")
                .modelo("Golf")
                .ano(2021)
                .cor("Cinza")
                .preco(new BigDecimal("120000"))
                .quilometragem(10000)
                .status(StatusVeiculo.DISPONIVEL)
                .build();

        Mockito.when(obterVeiculoPorIdUseCase.execute(eq(id))).thenReturn(Optional.of(v));

        mockMvc.perform(get("/veiculo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("VW"))
                .andExpect(jsonPath("$.modelo").value("Golf"));
    }

    @Test
    void getById_quandoNaoExiste_entao404() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(obterVeiculoPorIdUseCase.execute(eq(id))).thenReturn(Optional.empty());

        mockMvc.perform(get("/veiculo/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ----- PUT /veiculo/{id} -----
    @Test
    void put_deveAtualizarCamposValidos_entao200() throws Exception {
        UUID id = UUID.randomUUID();
        Veiculo atualizado = Veiculo.builder()
                .id(id)
                .marca("Honda")
                .modelo("Civic")
                .ano(2020)
                .cor("Branco")
                .preco(new BigDecimal("88000"))
                .quilometragem(20000)
                .status(StatusVeiculo.DISPONIVEL)
                .build();

        Mockito.when(alterarVeiculoUseCase.execute(eq(id), any(Veiculo.class))).thenReturn(atualizado);

        Veiculo request = Veiculo.builder()
                .marca("Honda")
                .modelo("Civic")
                .ano(2020)
                .cor("Branco")
                .preco(new BigDecimal("88000"))
                .quilometragem(20000)
                .build();

        mockMvc.perform(put("/veiculo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cor").value("Branco"))
                .andExpect(jsonPath("$.preco").value(88000.0));
    }

    @Test
    void put_quandoNaoEncontrado_entao404_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"))
                .when(alterarVeiculoUseCase).execute(eq(id), any(Veiculo.class));

        Veiculo request = Veiculo.builder()
                .marca("Honda")
                .modelo("Civic")
                .ano(2020)
                .cor("Branco")
                .preco(new BigDecimal("88000"))
                .quilometragem(20000)
                .build();

        mockMvc.perform(put("/veiculo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Veiculo não encontrado"));
    }

    @Test
    void put_quandoInvalido_entao400_porValidacao() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/veiculo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ----- DELETE /veiculo/{id} -----
    @Test
    void delete_quandoSucesso_entao204_eUsecaseChamado() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/veiculo/{id}", id))
                .andExpect(status().isNoContent());

        verify(deletarVeiculo).execute(id);
    }

    @Test
    void delete_quandoNaoEncontrado_entao404_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"))
                .when(deletarVeiculo).execute(id);

        mockMvc.perform(delete("/veiculo/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Veiculo não encontrado"));
    }

    @Test
    void delete_quandoVendido_entao400_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new VeiculoVendidoException("Não é possível deletar um veículo já vendido"))
                .when(deletarVeiculo).execute(id);

        mockMvc.perform(delete("/veiculo/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Não é possível deletar um veículo já vendido"));
    }
}
