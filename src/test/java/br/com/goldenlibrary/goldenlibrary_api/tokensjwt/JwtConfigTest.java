package br.com.goldenlibrary.goldenlibrary_api.tokensjwt;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
    void shouldLoadAllBeansSuccessfullyWhenSecretIsConfigured() {
        assertNotNull(jwtConfig);
        assertNotNull(jwtSecretKey);
        assertNotNull(jwtEncoder);
        assertNotNull(jwtDecoder);
        assertEquals("HmacSHA256", jwtSecretKey.getAlgorithm());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSecretIsNull() {

        JwtConfig configMock = new JwtConfig();

        ReflectionTestUtils.setField(configMock, "jwtSecretRaw", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configMock.jwtSecretKey();
        });

        assertTrue(exception.getMessage().contains("nao foi configurada"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSecretIsEmptyOrBlank() {

        JwtConfig configMock = new JwtConfig();

        ReflectionTestUtils.setField(configMock, "jwtSecretRaw", "   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            configMock.jwtSecretKey();
        });

        assertTrue(exception.getMessage().contains("nao foi configurada"));
    }
}