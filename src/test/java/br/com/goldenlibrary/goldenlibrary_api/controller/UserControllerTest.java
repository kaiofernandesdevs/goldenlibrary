package br.com.goldenlibrary.goldenlibrary_api.controller;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import br.com.goldenlibrary.goldenlibrary_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController — testes E2E / Caixa Preta com Testcontainers")
class UserControllerTest extends MongoIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void limparBanco() {
        userRepository.deleteAll();
    }

    // ═══════════════════════════════════════════════════════════════════
    // POST /user/signup — cadastro
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve cadastrar usuário e retornar 201 — UC-001 Fluxo 1.1")
    void deveCadastrarUsuario() throws Exception {
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kaio",
                                  "email": "kaio@email.com",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Kaio"))
                .andExpect(jsonPath("$.email").value("kaio@email.com"))
                // Senha nunca deve aparecer na resposta
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("deve retornar 400 para e-mail duplicado — UC-001 E1")
    void deveRetornar400ParaEmailDuplicado() throws Exception {
        String body = """
                {
                  "name": "Kaio",
                  "email": "kaio@email.com",
                  "password": "senha123"
                }
                """;

        // Primeiro cadastro — sucesso
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Segundo cadastro com mesmo e-mail — deve falhar
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("E-mail passado já está cadastrado"));
    }

    @Test
    @DisplayName("deve retornar 400 quando nome está em branco — UC-001 E2")
    void deveRetornar400NomeEmBranco() throws Exception {
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "kaio@email.com",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve retornar 400 quando e-mail é inválido — UC-001 E2")
    void deveRetornar400EmailInvalido() throws Exception {
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kaio",
                                  "email": "email-invalido",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve retornar 400 quando senha tem menos de 6 caracteres — UC-001 E2")
    void deveRetornar400SenhaCurta() throws Exception {
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kaio",
                                  "email": "kaio@email.com",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ═══════════════════════════════════════════════════════════════════
    // POST /user/login — autenticação
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve autenticar e retornar token JWT — UC-001 Fluxo 1.2")
    void deveAutenticarERetornarToken() throws Exception {
        // Cadastra primeiro
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kaio",
                                  "email": "kaio@email.com",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isCreated());

        // Faz login
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "kaio@email.com",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Kaio"));
    }

    @Test
    @DisplayName("deve retornar 401 para credenciais inválidas — UC-001 Fluxo 1.2")
    void deveRetornar401CredenciaisInvalidas() throws Exception {
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "naoexiste@email.com",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    @Test
    @DisplayName("deve retornar 401 para senha incorreta — UC-001 Fluxo 1.2")
    void deveRetornar401SenhaIncorreta() throws Exception {
        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kaio",
                                  "email": "kaio@email.com",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "kaio@email.com",
                                  "password": "senhaerrada"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}