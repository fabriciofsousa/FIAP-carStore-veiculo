package br.com.fiap.veiculo.controller.veiculo;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

import br.com.fiap.veiculo.controller.veiculo.dto.VeiculoRequestDTO;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class VeiculoControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        RestAssured.registerParser("text/plain", io.restassured.parsing.Parser.TEXT);
    }

    @Nested
    class CadastroVeiculo {

        @Test
        void deveCriarVeiculoValido() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", 2020, "Prata", new BigDecimal("95000"), 15000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculo")
                    .then()
                    .statusCode(HttpStatus.CREATED.value()) // <-- controller retorna CREATED
                    .body("id", notNullValue())
                    .body("marca", equalTo("Toyota"))
                    .body("modelo", equalTo("Corolla"))
                    .body("ano", equalTo(2020))
                    .body("cor", equalTo("Prata"))
                    .body("preco", notNullValue())
                    .body("quilometragem", equalTo(15000));
        }

        @Test
        void naoDeveCriarVeiculoComMarcaVazia() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "", "ModeloX", 2020, "Prata", new BigDecimal("50000"), 10000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculo")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value()); // validação DTO => 400
        }

        @Test
        void naoDeveCriarVeiculoComModeloVazio() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "", 2020, "Prata", new BigDecimal("50000"), 10000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculo")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void naoDeveCriarVeiculoComAnoInvalido() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", 1800, "Prata", new BigDecimal("50000"), 10000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculo")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value()); // validação DTO (Min) => 400
        }

        @Test
        void naoDeveCriarVeiculoComPrecoNegativo() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", 2020, "Prata", new BigDecimal("-5000"), 10000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculo")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value()); // validação DTO (Positive) => 400
        }

        @Test
        void naoDeveCriarVeiculoComQuilometragemNegativa() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Toyota", "Corolla", 2020, "Prata", new BigDecimal("50000"), -100
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(veiculo)
                    .when()
                    .post("/veiculo")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value()); // validação DTO (PositiveOrZero) => 400
        }
    }

    @Nested
    class ConsultaVeiculos {

        @Test
        void deveObterVeiculoPorId() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Volkswagen", "Golf", 2021, "Cinza", new BigDecimal("120000"), 10000
            );

            // cria e recupera id do corpo JSON
            UUID id = UUID.fromString(
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .body(veiculo)
                            .when()
                            .post("/veiculo")
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract().path("id")
            );

            given()
                    .when()
                    .get("/veiculo/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("marca", equalTo("Volkswagen"))
                    .body("modelo", equalTo("Golf"));
        }

        @Test
        void naoDeveObterVeiculoInexistentePorId() {
            UUID randomId = UUID.randomUUID();

            given()
                    .when()
                    .get("/veiculo/{id}", randomId.toString())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value()); // controller retorna 404 sem corpo
        }

        @Test
        void deveListarVeiculosPorStatusDisponivel() {
            given()
                    .when()
                    .get("/veiculo/status/DISPONIVEL")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }

        @Test
        void deveListarVeiculosPorStatusVendido() {
            given()
                    .when()
                    .get("/veiculo/status/VENDIDO")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", notNullValue());
        }
    }

    @Nested
    class AtualizacaoVeiculo {

        @Test
        void deveAtualizarCamposValidos() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Honda", "Civic", 2019, "Preto", new BigDecimal("85000"), 20000
            );

            UUID id = UUID.fromString(
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .body(veiculo)
                            .when()
                            .post("/veiculo")
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract().path("id")
            );

            VeiculoRequestDTO updateVeiculo = new VeiculoRequestDTO(
                    "Honda", "Civic", 2019, "Branco", new BigDecimal("88000"), 20000
            );

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(updateVeiculo)
                    .when()
                    .put("/veiculo/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("cor", equalTo("Branco"))
                    .body("preco", notNullValue());
        }
    }

    @Nested
    class DelecaoVeiculo {

        @Test
        void deveDeletarVeiculoExistente() {
            VeiculoRequestDTO veiculo = new VeiculoRequestDTO(
                    "Ford", "Focus", 2018, "Azul", new BigDecimal("60000"), 30000
            );

            UUID id = UUID.fromString(
                    given()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .body(veiculo)
                            .when()
                            .post("/veiculo")
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract().path("id")
            );

            given()
                    .when()
                    .delete("/veiculo/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
