package br.com.goldenlibrary.goldenlibrary_api.service;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import br.com.goldenlibrary.goldenlibrary_api.repository.UserRepository;
import br.com.goldenlibrary.goldenlibrary_api.service.UserService.EmailAlreadyExistsException;
import br.com.goldenlibrary.goldenlibrary_api.service.UserService.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UserService — testes de integração com Testcontainers")
class UserServiceTest extends MongoIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    // ═══════════════════════════════════════════════════════════════════
    // addNewUser — happy path
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve cadastrar usuário com senha criptografada — UC-001 Fluxo 1.1")
    void shouldRegisterUserWithEncryptedPassword() {
        RegisterRequest request = new RegisterRequest("Kaio", "kaio@email.com", "senha123");

        User savedUser = userService.addNewUser(request);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Kaio");
        assertThat(savedUser.getEmail()).isEqualTo("kaio@email.com");
        // Senha deve estar criptografada — nunca igual ao plain text
        assertThat(savedUser.getPassword()).isNotEqualTo("senha123");
        assertThat(passwordEncoder.matches("senha123", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("deve persistir usuário no MongoDB")
    void shouldPersistUserInTheDatabase() {
        RegisterRequest request = new RegisterRequest("Kaio", "kaio@email.com", "senha123");

        User savedUser = userService.addNewUser(request);

        assertThat(userRepository.findById(savedUser.getId())).isPresent();
    }

    // ═══════════════════════════════════════════════════════════════════
    // addNewUser — e-mail duplicado (UC-001 E1)
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve lançar EmailAlreadyExistsException para e-mail duplicado — UC-001 E1")
    void shouldThrowExceptionForDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Kaio", "kaio@email.com", "senha123");
        userService.addNewUser(request);

        assertThatThrownBy(() -> userService.addNewUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("E-mail passado já está cadastrado");
    }

    @Test
    @DisplayName("deve aceitar e-mails diferentes para usuários distintos")
    void shouldAcceptDifferentEmailsForDistinctUsers() {
        userService.addNewUser(new RegisterRequest("Kaio", "kaio@email.com", "senha123"));
        userService.addNewUser(new RegisterRequest("Outro", "outro@email.com", "senha456"));

        assertThat(userRepository.count()).isEqualTo(2);
    }

    // ═══════════════════════════════════════════════════════════════════
    // addNewUser — caixa branca: role padrão
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve atribuir role USER por padrão ao cadastrar")
    void shouldAssignDefaultUserRoleWhenRegistering() {
        User savedUser = userService.addNewUser(
                new RegisterRequest("Kaio", "kaio@email.com", "senha123"));

        assertThat(savedUser.getRole()).isNotNull();
        assertThat(savedUser.getRole().name()).isEqualTo("USER");
    }
    // ═══════════════════════════════════════════════════════════════════
    // UserRepository — queries customizadas
    // ═══════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("existsByEmail deve retornar true para e-mail cadastrado")
    void existsByEmailShouldReturnTrueForRegisteredEmail() {
        userService.addNewUser(new RegisterRequest("Kaio", "kaio@email.com", "senha123"));

        assertThat(userRepository.existsByEmail("kaio@email.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail deve retornar false para e-mail não cadastrado")
    void existsByEmailShouldReturnFalseForUnregisteredEmail() {
        assertThat(userRepository.existsByEmail("naoexiste@email.com")).isFalse();
    }

    @ParameterizedTest(name = "e-mail válido: {0}")
    @ValueSource(strings = {
            "usuario@gmail.com",
            "usuario@empresa.com.br",
            "usuario.nome@dominio.org"
    })
    @DisplayName("deve aceitar diferentes formatos de e-mail válidos")
    void shouldAcceptDifferentValidEmailFormats(String email) {
        User savedUser = userService.addNewUser(
                new RegisterRequest("Usuário", email, "senha123"));

        assertThat(savedUser.getEmail()).isEqualTo(email);
    }
}