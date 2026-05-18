package br.com.goldenlibrary.goldenlibrary_api.controller;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
import br.com.goldenlibrary.goldenlibrary_api.enums.UserRole;
import br.com.goldenlibrary.goldenlibrary_api.repository.BookRepository;
import br.com.goldenlibrary.goldenlibrary_api.repository.UserRepository;
import br.com.goldenlibrary.goldenlibrary_api.security.CustomUserDetails;
import br.com.goldenlibrary.goldenlibrary_api.service.BookService;
import br.com.goldenlibrary.goldenlibrary_api.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookController — testes E2E / Caixa Preta com Testcontainers")
class BookControllerTest extends MongoIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private BookService bookService;

    private String tokenUser1;
    private String tokenUser2;
    private String userId1;
    private String userId2;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Usuário 1
        User user1 = userRepository.save(new User(
                "Kaio", "kaio@email.com",
                passwordEncoder.encode("senha123"), UserRole.USER));
        userId1 = user1.getId();
        tokenUser1 = jwtService.newToken(new CustomUserDetails(user1));

        // Usuário 2
        User user2 = userRepository.save(new User(
                "Outro", "outro@email.com",
                passwordEncoder.encode("senha123"), UserRole.USER));
        userId2 = user2.getId();
        tokenUser2 = jwtService.newToken(new CustomUserDetails(user2));
    }

    // ═══════════════════════════════════════════════════════════════════
    // POST /books — criar livro
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve criar livro e retornar 201 — UC-002")
    void deveCriarLivro() throws Exception {
        String body = objectMapper.writeValueAsString(
                livroRequest("Dom Casmurro", "Machado", "Romance", ReadingStatus.WANT_TO_READ));

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Dom Casmurro"))
                .andExpect(jsonPath("$.userId").value(userId1));
    }

    @Test
    @DisplayName("deve retornar 401 ao criar livro sem token — UC-002 E1")
    void deveRetornar401SemToken() throws Exception {
        String body = objectMapper.writeValueAsString(
                livroRequest("Livro", "Autor", "Gênero", ReadingStatus.READ));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve retornar 400 quando campos obrigatórios ausentes — UC-002 E2")
    void deveRetornar400CamposAusentes() throws Exception {
        // Livro sem título
        String body = """
                {
                  "author": "Autor",
                  "genre": "Gênero",
                  "status": "READ"
                }
                """;

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ═══════════════════════════════════════════════════════════════════
    // GET /books — listar livros
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve listar apenas livros do usuário autenticado — RF004")
    void deveListarApenasLivrosDoUsuario() throws Exception {
        bookService.createBook(livro("Livro A", ReadingStatus.READ), userId1);
        bookService.createBook(livro("Livro B", ReadingStatus.READING), userId1);
        bookService.createBook(livro("Livro C", ReadingStatus.WANT_TO_READ), userId2);

        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is(userId1))));
    }

    @Test
    @DisplayName("deve buscar livro por título parcial — UC-003")
    void deveBuscarPorTitulo() throws Exception {
        bookService.createBook(livro("O Senhor dos Anéis", ReadingStatus.READ), userId1);
        bookService.createBook(livro("O Hobbit", ReadingStatus.WANT_TO_READ), userId1);

        mockMvc.perform(get("/books?title=senhor")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("O Senhor dos Anéis"));
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há livros — UC-003 E1")
    void deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ═══════════════════════════════════════════════════════════════════
    // GET /books/filter — filtrar por status
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve filtrar livros por status — UC-003")
    void deveFiltrarPorStatus() throws Exception {
        bookService.createBook(livro("Livro A", ReadingStatus.READING), userId1);
        bookService.createBook(livro("Livro B", ReadingStatus.READ), userId1);

        mockMvc.perform(get("/books/filter?status=READING")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("READING"));
    }

    // ═══════════════════════════════════════════════════════════════════
    // PUT /books/{id} — atualizar livro
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve atualizar livro e retornar 200 — UC-002")
    void deveAtualizarLivro() throws Exception {
        Book salvo = bookService.createBook(livro("Título Antigo", ReadingStatus.WANT_TO_READ), userId1);

        String body = objectMapper.writeValueAsString(
                livroRequest("Título Novo", "Autor", "Gênero", ReadingStatus.READING));

        mockMvc.perform(put("/books/" + salvo.getId())
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título Novo"))
                .andExpect(jsonPath("$.status").value("READING"));
    }

    @Test
    @DisplayName("deve retornar 404 ao atualizar livro de outro usuário")
    void deveRetornar404AoAtualizarLivroDeOutroUsuario() throws Exception {
        Book salvo = bookService.createBook(livro("Livro", ReadingStatus.READ), userId2);

        String body = objectMapper.writeValueAsString(
                livroRequest("Novo", "Autor", "Gênero", ReadingStatus.READING));

        mockMvc.perform(put("/books/" + salvo.getId())
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════════════
    // DELETE /books/{id} — deletar livro
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve deletar livro e retornar 204 — UC-002")
    void deveDeletarLivro() throws Exception {
        Book salvo = bookService.createBook(livro("Livro", ReadingStatus.READ), userId1);

        mockMvc.perform(delete("/books/" + salvo.getId())
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deve retornar 404 ao deletar livro inexistente — UC-002")
    void deveRetornar404AoDeletarLivroInexistente() throws Exception {
        mockMvc.perform(delete("/books/id-que-nao-existe")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve retornar 403 ao deletar livro de outro usuário — UC-002 E3")
    void deveRetornar403AoDeletarLivroDeOutroUsuario() throws Exception {
        Book salvo = bookService.createBook(livro("Livro", ReadingStatus.READ), userId2);

        mockMvc.perform(delete("/books/" + salvo.getId())
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isForbidden());
    }

    // ═══════════════════════════════════════════════════════════════════
    // helpers
    // ═══════════════════════════════════════════════════════════════════

    private Book livro(String title, ReadingStatus status) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor("Autor Padrão");
        b.setGenre("Ficção");
        b.setStatus(status);
        return b;
    }

    private record BookRequest(String title, String author, String genre, ReadingStatus status) {}

    private BookRequest livroRequest(String title, String author, String genre, ReadingStatus status) {
        return new BookRequest(title, author, genre, status);
    }
}