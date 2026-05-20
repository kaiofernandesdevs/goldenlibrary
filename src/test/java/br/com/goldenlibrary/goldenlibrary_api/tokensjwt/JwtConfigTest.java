package br.com.goldenlibrary.goldenlibrary_api.tokensjwt;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import br.com.goldenlibrary.goldenlibrary_api.enums.UserRole;
import br.com.goldenlibrary.goldenlibrary_api.security.CustomUserDetails;
import br.com.goldenlibrary.goldenlibrary_api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JwtConfigTest extends MongoIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateAndExtractUserFromTokenSuccessfully() {
        User user = new User();
        user.setId("12345");
        user.setName("Kaio Teste");
        user.setEmail("test@goldenlibrary.com");
        user.setRole(UserRole.USER);

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null
        );

        String token = jwtService.newToken(userDetails);
        assertNotNull(token);

        CustomUserDetails extractedUser = jwtService.getUserInToken(token);
        assertNotNull(extractedUser);

        assertEquals("test@goldenlibrary.com", extractedUser.getUsername());
        assertEquals("Kaio Teste", extractedUser.getName());
        assertEquals("12345", extractedUser.getId());
    }
}