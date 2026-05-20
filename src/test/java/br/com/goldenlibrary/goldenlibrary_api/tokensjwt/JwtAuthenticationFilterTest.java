package br.com.goldenlibrary.goldenlibrary_api.tokensjwt;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import br.com.goldenlibrary.goldenlibrary_api.enums.UserRole;
import br.com.goldenlibrary.goldenlibrary_api.repository.UserRepository;
import br.com.goldenlibrary.goldenlibrary_api.security.CustomUserDetails;
import br.com.goldenlibrary.goldenlibrary_api.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTest extends MongoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPassFilterWhenAuthorizationHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldPassFilterWhenTokenIsTooShort() throws Exception {
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValidWithRealDatabase() throws Exception {
        User user = new User();
        user.setId("67890");
        user.setName("Kaio Fernandes");
        user.setEmail("kaio@goldenlibrary.com");
        user.setPassword("senha123");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );

        String validToken = jwtService.newToken(userDetails);
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldClearContextWhenTokenExceptionOccurs() throws Exception {
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer token_totalmente_invalido_que_vai_dar_erro_no_decoder"))
                .andExpect(status().isForbidden());
    }
}