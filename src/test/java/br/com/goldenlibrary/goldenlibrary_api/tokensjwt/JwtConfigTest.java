package br.com.goldenlibrary.goldenlibrary_api.tokensjwt;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JwtConfig — Testes de Configuração do JWT")
public class JwtConfigTest extends MongoIntegrationTest {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private SecretKey jwtSecretKey;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Deve carregar todos os beans com sucesso quando o secret estiver configurado")
    void shouldLoadAllBeansSuccessfullyWhenSecretIsConfigured() {
        assertNotNull(jwtConfig);
        assertNotNull(jwtSecretKey);
        assertNotNull(jwtEncoder);
        assertNotNull(jwtDecoder);
        assertEquals("HmacSHA256", jwtSecretKey.getAlgorithm());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o secret for nulo")
    void shouldThrowIllegalArgumentExceptionWhenSecretIsNull() {

        JwtConfig configMock = new JwtConfig();

        ReflectionTestUtils.setField(configMock, "jwtSecretRaw", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configMock.jwtSecretKey();
        });

        assertTrue(exception.getMessage().contains("nao foi configurada"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o secret estiver vazio ou em branco")
    void shouldThrowIllegalArgumentExceptionWhenSecretIsEmptyOrBlank() {

        JwtConfig configMock = new JwtConfig();

        ReflectionTestUtils.setField(configMock, "jwtSecretRaw", "   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configMock.jwtSecretKey();
        });

        assertTrue(exception.getMessage().contains("nao foi configurada"));
    }
}