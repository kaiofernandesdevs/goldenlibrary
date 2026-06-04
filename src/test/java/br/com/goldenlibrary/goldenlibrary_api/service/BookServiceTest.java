package br.com.goldenlibrary.goldenlibrary_api.service;

import br.com.goldenlibrary.goldenlibrary_api.MongoIntegrationTest;
import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
import br.com.goldenlibrary.goldenlibrary_api.repository.BookRepository;
import br.com.goldenlibrary.goldenlibrary_api.service.BookService.DeleteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BookService — testes de integração com Testcontainers")
class BookServiceTest extends MongoIntegrationTest {

    @Autowired private BookService bookService;
    @Autowired private BookRepository bookRepository;

    private static final String USER_ID       = "user-001";
    private static final String OTHER_USER_ID = "user-002";

    @BeforeEach
    void clearDatabase() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("deve criar livro com userId do token e timestamps preenchidos")
    void shouldCreateBookWithUserIdAndTimestamps() {
        Book book = buildBook("Dom Casmurro", "Machado de Assis", ReadingStatus.WANT_TO_READ);
        Book saved = bookService.createBook(book, USER_ID);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getTitle()).isEqualTo("Dom Casmurro");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("deve retornar apenas livros do userId informado")
    void shouldReturnOnlyBooksFromUser() {
        bookService.createBook(buildBook("Book A", "Author", ReadingStatus.READING), USER_ID);
        bookService.createBook(buildBook("Book B", "Author", ReadingStatus.READ), USER_ID);
        bookService.createBook(buildBook("Book C", "Other",  ReadingStatus.WANT_TO_READ), OTHER_USER_ID);

        List<Book> result = bookService.getAllBooks(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(b -> b.getUserId().equals(USER_ID));
    }

    @Test
    @DisplayName("deve retornar lista vazia quando usuário não tem livros — UC-003 E1")
    void shouldReturnEmptyListWhenNoBooksExist() {
        assertThat(bookService.getAllBooks(USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("deve buscar livro por título parcial case-insensitive — UC-003")
    void shouldSearchByPartialTitleCaseInsensitive() {
        bookService.createBook(buildBook("The Lord of the Rings", "Tolkien", ReadingStatus.READ), USER_ID);
        bookService.createBook(buildBook("The Hobbit", "Tolkien", ReadingStatus.READ), USER_ID);
        bookService.createBook(buildBook("Harry Potter", "Rowling", ReadingStatus.READING), USER_ID);

        List<Book> result = bookService.searchByTitle(USER_ID, "lord");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("The Lord of the Rings");
    }

    @Test
    @DisplayName("não deve retornar livros de outro usuário na busca por título")
    void shouldNotReturnOtherUsersBooksOnTitleSearch() {
        bookService.createBook(buildBook("Dom Casmurro", "Machado", ReadingStatus.READ), OTHER_USER_ID);
        assertThat(bookService.searchByTitle(USER_ID, "Dom")).isEmpty();
    }

    @ParameterizedTest(name = "deve filtrar por status {0}")
    @EnumSource(ReadingStatus.class)
    @DisplayName("deve filtrar corretamente por cada status de leitura — UC-003")
    void shouldFilterByEachReadingStatus(ReadingStatus status) {
        bookService.createBook(buildBook("Book A", "Author", status), USER_ID);
        bookService.createBook(buildBook("Book B", "Author", ReadingStatus.WANT_TO_READ), OTHER_USER_ID);

        List<Book> result = bookService.filterByStatus(USER_ID, status);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(status);
        assertThat(result.get(0).getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("deve retornar livro pelo id quando pertence ao usuário")
    void shouldReturnBookByIdWhenBelongsToUser() {
        Book saved = bookService.createBook(buildBook("Book X", "Author", ReadingStatus.READ), USER_ID);
        Optional<Book> result = bookService.getBookById(saved.getId(), USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("não deve retornar livro de outro usuário pelo id — isolamento RF004")
    void shouldNotReturnAnotherUsersBookById() {
        Book saved = bookService.createBook(buildBook("Book X", "Author", ReadingStatus.READ), OTHER_USER_ID);
        assertThat(bookService.getBookById(saved.getId(), USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("deve atualizar livro com novos dados — UC-002")
    void shouldUpdateBookWithNewData() {
        Book saved = bookService.createBook(buildBook("Old Title", "Author", ReadingStatus.WANT_TO_READ), USER_ID);
        Optional<Book> updated = bookService.updateBook(saved.getId(),
                buildBook("New Title", "New Author", ReadingStatus.READING), USER_ID);

        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("New Title");
        assertThat(updated.get().getStatus()).isEqualTo(ReadingStatus.READING);
    }

    @Test
    @DisplayName("não deve atualizar livro de outro usuário — UC-002")
    void shouldNotUpdateAnotherUsersBook() {
        Book saved = bookService.createBook(buildBook("Book", "Author", ReadingStatus.READ), OTHER_USER_ID);
        assertThat(bookService.updateBook(saved.getId(),
                buildBook("New", "New", ReadingStatus.READING), USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("deve deletar livro e retornar DELETED — UC-002")
    void shouldDeleteBookAndReturnDeleted() {
        Book saved = bookService.createBook(buildBook("Book", "Author", ReadingStatus.READ), USER_ID);

        assertThat(bookService.deleteBook(saved.getId(), USER_ID)).isEqualTo(DeleteResult.DELETED);
        assertThat(bookRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("deve retornar NOT_FOUND quando livro não existe — UC-002")
    void shouldReturnNotFoundWhenBookDoesNotExist() {
        assertThat(bookService.deleteBook("non-existent-id", USER_ID)).isEqualTo(DeleteResult.NOT_FOUND);
    }

    @Test
    @DisplayName("deve retornar FORBIDDEN quando livro pertence a outro usuário — UC-002 E3")
    void shouldReturnForbiddenWhenBookBelongsToAnotherUser() {
        Book saved = bookService.createBook(buildBook("Book", "Author", ReadingStatus.READ), OTHER_USER_ID);

        assertThat(bookService.deleteBook(saved.getId(), USER_ID)).isEqualTo(DeleteResult.FORBIDDEN);
        assertThat(bookRepository.findById(saved.getId())).isPresent();
    }

    private Book buildBook(String title, String author, ReadingStatus status) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setGenre("Fiction");
        b.setStatus(status);
        return b;
    }
}