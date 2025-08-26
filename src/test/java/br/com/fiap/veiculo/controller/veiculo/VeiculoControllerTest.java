package br.com.fiap.veiculo.controller.veiculo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.usecase.veiculo.*;

class VeiculoControllerTest {

    private MockMvc mockMvc;
    private AutoCloseable mocks;

    @Mock
    private CriarVeiculoUseCase criarVeiculoUseCase;
    @Mock
    private ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase;
    @Mock
    private ObterVeiculoUseCase obterVeiculoUseCase;
    @Mock
    private AlterarVeiculoUseCase alterarVeiculoUseCase;
    @Mock
    private DeletarVeiculoUseCase deletarVeiculo;

    @InjectMocks
    private VeiculoController veiculoController;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Nested
    class CadastroVeiculo {

        @Test
        void deveCriarVeiculoValido() throws Exception {
            UUID id = UUID.randomUUID();
            Veiculo veiculo = Veiculo.builder()
                    .id(id)
                    .marca("Toyota")
                    .modelo("Corolla")
                    .ano(2020)
                    .cor("Prata")
                    .preco(new BigDecimal(95000.0))
                    .quilometragem(15000)
                    .status(StatusVeiculo.DISPONIVEL)
                    .build();

            when(criarVeiculoUseCase.execute(any(Veiculo.class))).thenReturn(veiculo);

            mockMvc.perform(post("/veiculos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(veiculo)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.marca").value("Toyota"))
                    .andExpect(jsonPath("$.modelo").value("Corolla"))
                    .andExpect(jsonPath("$.status").value(StatusVeiculo.DISPONIVEL.name()));
        }

        @Test
        void naoDeveCriarVeiculoComCamposInvalidos() throws Exception {
            Veiculo veiculo = Veiculo.builder()
                    .marca("")
                    .modelo("")
                    .ano(1800)
                    .cor("")
                    .preco(new BigDecimal(-100))
                    .quilometragem(-1)
                    .build();

            mockMvc.perform(post("/veiculos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(veiculo)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void naoDeveCriarVeiculoComCamposNull() throws Exception {
            Veiculo veiculo = Veiculo.builder().build();

            mockMvc.perform(post("/veiculos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(veiculo)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class AtualizacaoVeiculo {

        @Test
        void deveAtualizarCamposValidos() throws Exception {
            UUID id = UUID.randomUUID();
            Veiculo atualizado = Veiculo.builder()
                    .id(id)
                    .marca("Honda")
                    .modelo("Civic")
                    .cor("Branco")
                    .preco(new BigDecimal(88000.0))
                    .quilometragem(20000)
                    .status(StatusVeiculo.DISPONIVEL)
                    .build();

            when(alterarVeiculoUseCase.execute(any(), any(Veiculo.class))).thenReturn(atualizado);

            mockMvc.perform(put("/veiculos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(atualizado)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cor").value("Branco"))
                    .andExpect(jsonPath("$.preco").value(88000.0));
        }

        @Test
        void naoDeveAtualizarVeiculoInexistente() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new RuntimeException("Veiculo não encontrado"))
                    .when(alterarVeiculoUseCase).execute(any(), any(Veiculo.class));

            mockMvc.perform(put("/veiculos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"cor\":\"Branco\"}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void naoDeveAtualizarStatusInvalido() throws Exception {
            UUID id = UUID.randomUUID();
            mockMvc.perform(put("/veiculos/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"status\":\"INVALIDO\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DelecaoVeiculo {

        @Test
        void deveDeletarVeiculoExistente() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(deletarVeiculo).execute(id);

            mockMvc.perform(delete("/veiculos/{id}", id))
                    .andExpect(status().isNoContent());
        }

        @Test
        void naoDeveDeletarVeiculoInexistente() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new RuntimeException("Veiculo não encontrado")).when(deletarVeiculo).execute(id);

            mockMvc.perform(delete("/veiculos/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void naoDeveDeletarVeiculoVendido() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new RuntimeException("Veiculo vendido")).when(deletarVeiculo).execute(id);

            mockMvc.perform(delete("/veiculos/{id}", id))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ConsultaVeiculos {

        @Test
        void deveObterVeiculoPorId() throws Exception {
            UUID id = UUID.randomUUID();
            Veiculo veiculo = Veiculo.builder()
                    .id(id)
                    .marca("Volkswagen")
                    .modelo("Golf")
                    .cor("Cinza")
                    .preco(new BigDecimal(120000.0))
                    .quilometragem(10000)
                    .status(StatusVeiculo.DISPONIVEL)
                    .build();

            when(obterVeiculoPorIdUseCase.execute(any())).thenReturn(Optional.of(veiculo));

            mockMvc.perform(get("/veiculos/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.marca").value("Volkswagen"))
                    .andExpect(jsonPath("$.modelo").value("Golf"));
        }

        @Test
        void naoDeveObterVeiculoInexistentePorId() throws Exception {
            UUID id = UUID.randomUUID();
            when(obterVeiculoPorIdUseCase.execute(any())).thenReturn(Optional.empty());

            mockMvc.perform(get("/veiculos/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deveListarTodosVeiculos() throws Exception {
            UUID id = UUID.randomUUID();
            Veiculo veiculo = Veiculo.builder()
                    .id(id)
                    .marca("Fiat")
                    .modelo("Cronos")
                    .preco(new BigDecimal(70000.0))
                    .status(StatusVeiculo.DISPONIVEL)
                    .build();

            when(obterVeiculoUseCase.execute()).thenReturn(List.of(veiculo));

            mockMvc.perform(get("/veiculos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
