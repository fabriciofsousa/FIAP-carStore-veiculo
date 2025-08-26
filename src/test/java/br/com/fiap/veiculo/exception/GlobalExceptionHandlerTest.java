package br.com.fiap.veiculo.exception;

import br.com.fiap.veiculo.config.GlobalExceptionHandler;
import br.com.fiap.veiculo.controller.veiculo.VeiculoController;
import br.com.fiap.veiculo.domain.Veiculo;
import br.com.fiap.veiculo.usecase.veiculo.AlterarVeiculoUseCase;
import br.com.fiap.veiculo.usecase.veiculo.CriarVeiculoUseCase;
import br.com.fiap.veiculo.usecase.veiculo.DeletarVeiculoUseCase;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculoPorIdUseCase;
import br.com.fiap.veiculo.usecase.veiculo.ObterVeiculoUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VeiculoController.class)
@Import(GlobalExceptionHandler.class)
class VeiculoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CriarVeiculoUseCase criarVeiculoUseCase;
    @MockBean private ObterVeiculoUseCase obterVeiculoUseCase;
    @MockBean private ObterVeiculoPorIdUseCase obterVeiculoPorIdUseCase;
    @MockBean private AlterarVeiculoUseCase alterarVeiculoUseCase;
    @MockBean private DeletarVeiculoUseCase deletarVeiculo;

    @Test
    void getById_quandoNaoEncontrado_entao404_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(obterVeiculoPorIdUseCase.execute(id))
                .thenThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"));

        mockMvc.perform(get("/veiculos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Veiculo não encontrado"));
    }

    @Test
    void getAll_quandoRuntimeException_entao400_comMensagemDoHandler() throws Exception {
        Mockito.when(obterVeiculoUseCase.execute())
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro inesperado"));
    }

    @Test
    void put_quandoNaoEncontrado_entao404_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(alterarVeiculoUseCase.execute(eq(id), any(Veiculo.class)))
                .thenThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"));

        // corpo mínimo só para passar pela desserialização
        mockMvc.perform(put("/veiculos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Veiculo não encontrado"));
    }

    @Test
    void post_quandoRuntimeException_entao400_comMensagemDoHandler() throws Exception {
        Mockito.when(criarVeiculoUseCase.execute(any(Veiculo.class)))
                .thenThrow(new RuntimeException("Erro ao criar"));

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Teste\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao criar"));
    }

    @Test
    void delete_quandoNaoEncontrado_entao404_comMensagemDoHandler() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new VeiculoNaoEncontradoException("Veiculo não encontrado"))
                .when(deletarVeiculo).execute(id);

        mockMvc.perform(delete("/veiculos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Veiculo não encontrado"));
    }

    @Test
    void delete_quandoSucesso_entao204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/veiculos/{id}", id))
                .andExpect(status().isNoContent());

        verify(deletarVeiculo).execute(id);
    }
}
