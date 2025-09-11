package br.com.fiap.veiculo.performance;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.time.Duration;
import java.util.UUID;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class PerformanceSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .header("Content-Type", "application/json");

    /**
     * Gera um JSON válido para VeiculoRequestDTO usando campos básicos.
     * Usamos uma lambda para garantir dados únicos por requisição (gera um sufixo com UUID curto).
     */
    private String createVeiculoJson(Session session) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return "{"
                + "\"marca\":\"Marca-" + suffix + "\","
                + "\"modelo\":\"Modelo-" + suffix + "\","
                + "\"ano\":2020,"
                + "\"cor\":\"Preto\","
                + "\"preco\":45000.50,"
                + "\"quilometragem\":10000"
                + "}";
    }

    // Action: criar veículo (verifica 201 e salva id retornado em jsonPath)
    ActionBuilder criarVeiculo = http("POST /veiculo - criar")
            .post("/veiculo")
            .body(StringBody(session -> createVeiculoJson(session)))
            .check(status().is(201))
            .check(io.gatling.javaapi.core.CoreDsl.jsonPath("$.id").saveAs("createdId"));

    // Action: obter veículo criado (usa ${createdId})
    ActionBuilder obterVeiculo = http("GET /veiculo/{id} - obter")
            .get(session -> "/veiculo/" + session.getString("createdId"))
            .check(status().is(200));

    // Action: deletar veículo criado (espera 204 No Content)
    ActionBuilder deletarVeiculo = http("DELETE /veiculo/{id} - deletar")
            .delete(session -> "/veiculo/" + session.getString("createdId"))
            .check(status().is(204));

    // Cenário que executa: criar -> obter -> deletar
    ScenarioBuilder cenarioFluxoCompleto = scenario("Fluxo Completo: criar -> obter -> deletar")
            .exec(criarVeiculo)
            .pause(Duration.ofMillis(100)) // pequena pausa para maior realismo
            .exec(obterVeiculo)
            .pause(Duration.ofMillis(50))
            .exec(deletarVeiculo);

    {
        setUp(
                // injeta carga open: aquecer, pico constante e desaceleração
                cenarioFluxoCompleto.injectOpen(
                        rampUsersPerSec(1).to(10).during(Duration.ofSeconds(15)), // aquecimento
                        constantUsersPerSec(10).during(Duration.ofSeconds(30)),     // carga estável
                        rampUsersPerSec(10).to(1).during(Duration.ofSeconds(15))   // desaceleração
                )
        )
                .protocols(httpProtocol)
                // SLAs / assertions
                .assertions(
                        global().responseTime().percentile4().lt(3000),          // 99th percentile < 3000ms
                        global().successfulRequests().percent().gt(95.0),          // >95% de sucesso
                        global().responseTime().mean().lt(800)                   // média < 800ms
                );
    }
}
