package br.com.fiap.veiculo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
		properties = {
				"spring.main.lazy-initialization=true",
				"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
		}
)
@EntityScan(basePackages = "br.com.fiap.veiculo.infra.database.entity.veiculo")
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"COGNITO_USER_POOL_ID=test-pool",
		"COGNITO_REGION=us-east-1",
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.us-east-1.amazonaws.com/test-pool"
})
class VeiculoApplicationTests {

	@Test
	void contextLoads() {
		// Teste para verificar se o contexto da aplicação carrega corretamente
	}

}
