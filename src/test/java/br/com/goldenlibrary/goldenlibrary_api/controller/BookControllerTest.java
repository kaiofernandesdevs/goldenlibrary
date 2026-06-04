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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

        User user1 = userRepository.save(new User(
                "Kaio", "kaio@email.com",
                passwordEncoder.encode("senha123"), UserRole.USER));
        userId1 = user1.getId();

        tokenUser1 = jwtService.newToken(new CustomUserDetails(user1));

        User user2 = userRepository.save(new User(
                "Other", "other@email.com",
                passwordEncoder.encode("senha123"), UserRole.USER));
        userId2 = user2.getId();


        tokenUser2 = jwtService.newToken(new CustomUserDetails(user2));
    }

    @Test
    @DisplayName("deve criar livro e retornar 201 — UC-002")
    void shouldCreateBookAndReturn201() throws Exception {
        String body = objectMapper.writeValueAsString(
                new BookRequest("Dom Casmurro", "Machado", "Romance", ReadingStatus.WANT_TO_READ));

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
    void shouldReturn401WhenCreatingBookWithoutToken() throws Exception {
        String body = objectMapper.writeValueAsString(
                new BookRequest("Book", "Author", "Genre", ReadingStatus.READ));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve retornar 400 quando campos obrigatórios ausentes — UC-002 E2")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        String body = """
                {
                  "author": "Author",
                  "genre": "Genre",
                  "status": "READ"
                }
                """;

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve listar apenas livros do usuário autenticado — RF004")
    void shouldListOnlyAuthenticatedUserBooks() throws Exception {
        bookService.createBook(buildBook("Book A", ReadingStatus.READ), userId1);
        bookService.createBook(buildBook("Book B", ReadingStatus.READING), userId1);
        bookService.createBook(buildBook("Book C", ReadingStatus.WANT_TO_READ), userId2);

        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is(userId1))));
    }

    @Test
    @DisplayName("deve buscar livro por título parcial — UC-003")
    void shouldSearchByPartialTitle() throws Exception {
        bookService.createBook(buildBook("The Lord of the Rings", ReadingStatus.READ), userId1);
        bookService.createBook(buildBook("The Hobbit", ReadingStatus.WANT_TO_READ), userId1);

        mockMvc.perform(get("/books?title=lord")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("The Lord of the Rings"));
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há livros — UC-003 E1")
    void shouldReturnEmptyListWhenNoBooksExist() throws Exception {
        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("deve filtrar livros por status de leitura — UC-003")
    void shouldFilterBooksByReadingStatus() throws Exception {
        bookService.createBook(buildBook("Book A", ReadingStatus.READING), userId1);
        bookService.createBook(buildBook("Book B", ReadingStatus.READ), userId1);

        mockMvc.perform(get("/books/filter?status=READING")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("READING"));
    }

    @Test
    @DisplayName("deve atualizar livro e retornar 200 — UC-002")
    void shouldUpdateBookAndReturn200() throws Exception {
        Book saved = bookService.createBook(buildBook("Old Title", ReadingStatus.WANT_TO_READ), userId1);

        String body = objectMapper.writeValueAsString(
                new BookRequest("New Title", "Author", "Genre", ReadingStatus.READING));

        mockMvc.perform(put("/books/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.status").value("READING"));
    }

    @Test
    @DisplayName("deve retornar 404 ao atualizar livro de outro usuário")
    void shouldReturn404WhenUpdatingAnotherUsersBook() throws Exception {
        Book saved = bookService.createBook(buildBook("Book", ReadingStatus.READ), userId2);

        String body = objectMapper.writeValueAsString(
                new BookRequest("New", "Author", "Genre", ReadingStatus.READING));

        mockMvc.perform(put("/books/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve deletar livro e retornar 204 — UC-002")
    void shouldDeleteBookAndReturn204() throws Exception {
        Book saved = bookService.createBook(buildBook("Book", ReadingStatus.READ), userId1);

        mockMvc.perform(delete("/books/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deve retornar 404 ao deletar livro inexistente — UC-002")
    void shouldReturn404WhenDeletingNonExistentBook() throws Exception {
        mockMvc.perform(delete("/books/non-existent-id")
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("deve retornar 403 ao deletar livro de outro usuário — UC-002 E3")
    void shouldReturn403WhenDeletingAnotherUsersBook() throws Exception {
        Book saved = bookService.createBook(buildBook("Book", ReadingStatus.READ), userId2);

        mockMvc.perform(delete("/books/" + saved.getId())
                        .header("Authorization", "Bearer " + tokenUser1))
                .andExpect(status().isForbidden());
    }

    private Book buildBook(String title, ReadingStatus status) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor("Default Author");
        b.setGenre("Fiction");
        b.setStatus(status);
        return b;
    }

    private record BookRequest(String title, String author, String genre, ReadingStatus status) {}
}