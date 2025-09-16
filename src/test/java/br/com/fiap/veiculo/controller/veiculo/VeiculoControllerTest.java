package br.com.fiap.veiculo.controller.veiculo;

import br.com.fiap.veiculo.config.GlobalExceptionHandler;
import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.exception.VeiculoNaoEncontradoException;
import br.com.fiap.veiculo.exception.VeiculoVendidoException;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import br.com.fiap.veiculo.infra.provider.VeiculoPovider;
import br.com.fiap.veiculo.usecase.veiculo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
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

    @Mock
    private VeiculoPovider veiculoGateway;

    @InjectMocks
    private VeiculoController veiculoController;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
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

            when(criarVeiculoUseCase.execute(any())).thenReturn(veiculo);

            mockMvc.perform(post("/veiculo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(veiculo)))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.marca").value("Toyota"))
                    .andExpect(jsonPath("$.modelo").value("Corolla"))
                    .andExpect(jsonPath("$.status").value(StatusVeiculo.DISPONIVEL.name()));
        }

        @Test
        void naoDeveCriarVeiculoComCamposInvalidos() throws Exception {
            VeiculoRequestDTO veiculoDTO = VeiculoRequestDTO.builder()
                    .marca("")
                    .modelo("")
                    .ano(1800)
                    .cor("")
                    .preco(new BigDecimal(-100))
                    .quilometragem(-1)
                    .build();

            mockMvc.perform(post("/veiculo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(veiculoDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void naoDeveCriarVeiculoComCamposNull() throws Exception {
            VeiculoRequestDTO veiculoDTO = new VeiculoRequestDTO();

            mockMvc.perform(post("/veiculo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(veiculoDTO)))
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
                    .ano(2020)
                    .cor("Branco")
                    .preco(new BigDecimal(88000.0))
                    .quilometragem(20000)
                    .status(StatusVeiculo.DISPONIVEL)
                    .build();

            when(alterarVeiculoUseCase.execute(any(), any(Veiculo.class))).thenReturn(atualizado);

            mockMvc.perform(put("/veiculo/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(atualizado)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cor").value("Branco"))
                    .andExpect(jsonPath("$.preco").value(88000.0));
        }


        @Test
        void naoDeveAtualizarVeiculoInexistente() throws Exception {
            UUID id = UUID.randomUUID();

            // corpo válido (tem todos os campos @NotNull/@NotBlank)
            Veiculo request = Veiculo.builder()
                    .marca("Honda")
                    .modelo("Civic")
                    .ano(2020)
                    .cor("Branco")
                    .preco(new BigDecimal(88000))
                    .quilometragem(20000)
                    .build();

            // quando o usecase for chamado, lança VeiculoNaoEncontradoException
            doThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"))
                    .when(alterarVeiculoUseCase).execute(any(), any());

            mockMvc.perform(put("/veiculo/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isNotFound());
        }


        @Test
        void naoDeveAtualizarStatusInvalido() throws Exception {
            UUID id = UUID.randomUUID();
            mockMvc.perform(put("/veiculo/{id}", id)
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

            mockMvc.perform(delete("/veiculo/{id}", id))
                    .andExpect(status().isNoContent());
        }

        @Test
        void naoDeveDeletarVeiculoInexistente() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"))
                    .when(deletarVeiculo).execute(id);

            mockMvc.perform(delete("/veiculo/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void naoDeveDeletarVeiculoVendido() throws Exception {
            UUID id = UUID.randomUUID();

            doThrow(new VeiculoVendidoException("Não é possível deletar um veículo já vendido"))
                    .when(deletarVeiculo).execute((id));

            mockMvc.perform(delete("/veiculo/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Não é possível deletar um veículo já vendido"));
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

            mockMvc.perform(get("/veiculo/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.marca").value("Volkswagen"))
                    .andExpect(jsonPath("$.modelo").value("Golf"));
        }

        @Test
        void naoDeveObterVeiculoInexistentePorId() throws Exception {
            UUID id = UUID.randomUUID();

            when(obterVeiculoPorIdUseCase.execute((id))).thenReturn(Optional.empty());

            mockMvc.perform(get("/veiculo/{id}", id))
                    .andExpect(status().isNotFound());
        }


    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // 👈 habilita LocalDateTime
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 👈 formata como ISO-8601
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}