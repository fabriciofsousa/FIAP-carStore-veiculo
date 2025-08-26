package br.com.fiap.veiculo.performance;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.time.Duration;

import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class PerformanceSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080").header("Content-Type",
            "application/json");

    ActionBuilder criarVeiculo = http("criar veiculo")
            .post("/veiculos")
            .body(StringBody("{ \"nome\": \"João Silva\", \"cpf\": \"123.456.789-00\" }"))
            .check(status().is(200));

    ScenarioBuilder cenarioCriarVeiculo = scenario("criar veiculo").exec(criarVeiculo);


    {

        setUp(
                cenarioCriarVeiculo.injectOpen(
                        rampUsersPerSec(1).to(10).during(Duration.ofSeconds(8)), // Aquecimento da aplicacao
                        constantUsersPerSec(10) // usuarios constantes usando
                                .during(Duration.ofSeconds(8)),
                        rampUsersPerSec(10) // Desaceleracao dos testes
                                .to(1)
                                .during(Duration.ofSeconds(8)))
        ).protocols(httpProtocol)
        .assertions(global().responseTime().max().lt(5000)); // Tempo maximo inferior de 1500 milesegundos

    }

}
