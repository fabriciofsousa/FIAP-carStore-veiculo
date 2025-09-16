package br.com.fiap.veiculo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@EntityScan(basePackages = "br.com.fiap.veiculo.infra.database.entity.veiculo")
@ActiveProfiles("test")
class VeiculoApplicationTests {

	@Test
	void contextLoads() {
		// Teste para verificar se o contexto da aplicação carrega corretamente
	}

}
