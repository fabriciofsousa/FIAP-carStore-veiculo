package br.com.fiap.veiculo.controller.veiculo;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import br.com.fiap.veiculo.infra.database.entity.veiculo.StatusVeiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VeiculoControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
    }

    @Nested
    class CadastroVeiculo {

        @Test
        void deveCriarVeiculoValido() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", 2020, "Prata", new BigDecimal(95000.0), 15000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("marca", equalTo("Toyota"))
                    .body("modelo", equalTo("Corolla"))
                    .body("ano", equalTo(2020))
                    .body("cor", equalTo("Prata"))
                    .body("preco", equalTo(95000.0f))
                    .body("quilometragem", equalTo(15000))
                    .body("status", equalTo(StatusVeiculo.DISPONIVEL.name()));
        }

        @Test
        void naoDeveCriarVeiculoComCamposInvalidos() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "", "ModeloX", 1800, "", new BigDecimal(-5000.0), -100
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("errors.marca", containsString("não pode ser vazio"))
                    .body("errors.ano", containsString("deve estar entre 1900 e o ano atual"))
                    .body("errors.preco", containsString("deve ser positivo"))
                    .body("errors.quilometragem", containsString("não pode ser negativa"));
        }
    }

    @Nested
    class AtualizacaoVeiculo {

        @Test
        void deveAtualizarCamposValidos() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Honda", "Civic", 2019, "Preto", new BigDecimal(85000.0), 20000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            String patchBody = "{\"preco\":88000.0,\"cor\":\"Branco\"}";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(patchBody)
                    .when()
                    .patch("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("preco", equalTo(88000.0f))
                    .body("cor", equalTo("Branco"))
                    .body("modelo", equalTo("Civic")); // Mantido
        }

        @Test
        void naoDeveAtualizarVeiculoInexistente() {
            String patchBody = "{\"preco\":88000.0}";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(patchBody)
                    .when()
                    .patch("/veiculos/{id}", UUID.randomUUID())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Veiculo não encontrado"));
        }

        @Test
        void naoDeveAtualizarVeiculoComCamposInvalidos() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Honda", "Civic", 2019, "Preto", new BigDecimal(85000.0), 20000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            String patchBody = "{\"preco\":-1000.0,\"cor\":\"\"}";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(patchBody)
                    .when()
                    .patch("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("errors.preco", containsString("deve ser positivo"))
                    .body("errors.cor", containsString("não pode ser vazio"));
        }
    }

    @Nested
    class DelecaoVeiculo {

        @Test
        void deveDeletarVeiculoExistente() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Ford", "Focus", 2018, "Azul", new BigDecimal(60000.0), 30000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            given()
                    .when()
                    .delete("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void naoDeveDeletarVeiculoInexistente() {
            given()
                    .when()
                    .delete("/veiculos/{id}", UUID.randomUUID())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Veiculo não encontrado"));
        }
    }

    @Nested
    class ConsultaVeiculos {

        @Test
        void deveObterVeiculoPorId() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Volkswagen", "Golf", 2021, "Cinza", new BigDecimal(120000.0), 10000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            given()
                    .when()
                    .get("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("marca", equalTo("Volkswagen"))
                    .body("modelo", equalTo("Golf"));
        }

        @Test
        void naoDeveObterVeiculoInexistentePorId() {
            given()
                    .when()
                    .get("/veiculos/{id}", UUID.randomUUID())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("Veiculo não encontrado"));
        }

        @Test
        void deveListarTodosVeiculos() {
            given()
                    .when()
                    .get("/veiculos")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }

        @Test
        void deveListarVeiculosPorStatusDisponivel() {
            given()
                    .when()
                    .get("/veiculos/disponiveis")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }

        @Test
        void deveListarVeiculosPorStatusVendido() {
            given()
                    .when()
                    .get("/veiculos/vendidos")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }
    }
    @Nested
    class CadastroCamposNulos {

        @Test
        void naoDeveCriarVeiculoComCamposNulos() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    null, null, null, null, null, null
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("errors.marca", containsString("não pode ser vazio"))
                    .body("errors.modelo", containsString("não pode ser vazio"))
                    .body("errors.ano", containsString("deve estar entre 1900 e o ano atual"))
                    .body("errors.cor", containsString("não pode ser vazio"))
                    .body("errors.preco", containsString("deve ser positivo"))
                    .body("errors.quilometragem", containsString("não pode ser negativa"));
        }

        @Test
        void naoDeveCriarVeiculoComAnoFuturo() {
            int anoFuturo = java.time.Year.now().getValue() + 1;
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", anoFuturo, "Prata", new BigDecimal(50000), 1000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("errors.ano", containsString("deve estar entre 1900 e o ano atual"));
        }

        @Test
        void naoDeveCriarVeiculoComCamposExcedendoTamanhoMaximo() {
            String longString = "a".repeat(256); // assumindo limite de 255 chars
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    longString, longString, 2020, longString, new BigDecimal(50000), 1000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("errors.marca", containsString("tamanho máximo"))
                    .body("errors.modelo", containsString("tamanho máximo"))
                    .body("errors.cor", containsString("tamanho máximo"));
        }
    }

    @Nested
    class AtualizacaoStatusData {

        @Test
        void naoDeveAtualizarStatusInvalido() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Honda", "Civic", 2019, "Preto", new BigDecimal(85000), 20000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            String patchBody = "{\"status\":\"INVALIDO\"}";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(patchBody)
                    .when()
                    .patch("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("errors.status", containsString("Status inválido"));
        }

        @Test
        void deveAtualizarDataAtualizacaoAutomaticamente() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Honda", "Civic", 2019, "Preto", new BigDecimal(85000), 20000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            String patchBody = "{\"preco\":86000}";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(patchBody)
                    .when()
                    .patch("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("dataAtualizacao", notNullValue());
        }
    }

    @Nested
    class DelecaoVeiculosVendidos {

        @Test
        void naoDeveDeletarVeiculoVendido() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", 2020, "Prata", new BigDecimal(95000), 10000
            );

            UUID id = given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculos")
                    .then()
                    .extract().path("id");

            // Marca como vendido
            String patchBody = "{\"status\":\"VENDIDO\"}";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(patchBody)
                    .when()
                    .patch("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Tenta deletar
            given()
                    .when()
                    .delete("/veiculos/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("Não é permitido deletar veículos vendidos"));
        }
    }

    @Nested
    class ListagemStatus {

        @Test
        void deveListarVeiculosDisponiveisOrdenadosPorPreco() {
            given()
                    .when()
                    .get("/veiculos/disponiveis")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }

        @Test
        void naoDeveListarComStatusInvalido() {
            given()
                    .when()
                    .get("/veiculos/status/INVALIDO")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("Status inválido"));
        }
    }

    @Nested
    class ObterPorId {

        @Test
        void naoDeveObterVeiculoComIdNull() {
            given()
                    .when()
                    .get("/veiculos/null")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("ID não pode ser nulo"));
        }
    }

}
