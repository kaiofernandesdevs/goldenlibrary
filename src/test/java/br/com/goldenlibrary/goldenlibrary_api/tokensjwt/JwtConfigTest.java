package br.com.goldenlibrary.goldenlibrary_api.tokensjwt;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class JwtConfigTest extends MongoIntegrationTest {

    @Test
    void shouldThrowExceptionWhenSecretIsNull() {
        JwtConfig jwtConfig = new JwtConfig();

        ReflectionTestUtils.setField(jwtConfig, "jwtSecretRaw", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtConfig.jwtSecretKey();
        });

        assertTrue(exception.getMessage().contains("A propriedade 'api.security.token.secret' nao foi configurada"));
    }


    @Test
    void shouldThrowExceptionWhenSecretIsEmpty() {
        JwtConfig jwtConfig = new JwtConfig();

        ReflectionTestUtils.setField(jwtConfig, "jwtSecretRaw", "   ");

        assertThrows(IllegalArgumentException.class, () -> {
            jwtConfig.jwtSecretKey();
        });
    }
}
