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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("BookService — testes de integração com Testcontainers")
class BookServiceTest extends MongoIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private static final String USER_ID      = "user-001";
    private static final String OTHER_USER_ID = "user-002";

    @BeforeEach
    void limparBanco() {
        bookRepository.deleteAll();
    }

    // ═══════════════════════════════════════════════════════════════════
    // createBook
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve criar livro com userId do token e timestamps preenchidos")
    void deveCriarLivroComUserIdETimestamps() {
        Book book = livro("Dom Casmurro", "Machado de Assis", ReadingStatus.WANT_TO_READ);

        Book salvo = bookService.createBook(book, USER_ID);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getUserId()).isEqualTo(USER_ID);
        assertThat(salvo.getTitle()).isEqualTo("Dom Casmurro");
        assertThat(salvo.getCreatedAt()).isNotNull();
        assertThat(salvo.getUpdatedAt()).isNotNull();
    }

    // ═══════════════════════════════════════════════════════════════════
    // getAllBooks
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve retornar apenas livros do userId informado")
    void deveRetornarApenasLivrosDoUsuario() {
        bookService.createBook(livro("Livro A", "Autor", ReadingStatus.READING), USER_ID);
        bookService.createBook(livro("Livro B", "Autor", ReadingStatus.READ), USER_ID);
        bookService.createBook(livro("Livro C", "Outro", ReadingStatus.WANT_TO_READ), OTHER_USER_ID);

        List<Book> resultado = bookService.getAllBooks(USER_ID);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(b -> b.getUserId().equals(USER_ID));
    }

    @Test
    @DisplayName("deve retornar lista vazia quando usuário não tem livros — UC-003 E1")
    void deveRetornarListaVaziaQuandoNaoHaLivros() {
        List<Book> resultado = bookService.getAllBooks(USER_ID);
        assertThat(resultado).isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════
    // searchByTitle
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve buscar livro por título parcial case-insensitive — UC-003")
    void deveBuscarPorTituloParcial() {
        bookService.createBook(livro("O Senhor dos Anéis", "Tolkien", ReadingStatus.READ), USER_ID);
        bookService.createBook(livro("O Hobbit", "Tolkien", ReadingStatus.READ), USER_ID);
        bookService.createBook(livro("Harry Potter", "Rowling", ReadingStatus.READING), USER_ID);

        List<Book> resultado = bookService.searchByTitle(USER_ID, "senhor");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitle()).isEqualTo("O Senhor dos Anéis");
    }

    @Test
    @DisplayName("não deve retornar livros de outro usuário na busca por título")
    void naoDeveRetornarLivrosDeOutroUsuarioNaBusca() {
        bookService.createBook(livro("Dom Casmurro", "Machado", ReadingStatus.READ), OTHER_USER_ID);

        List<Book> resultado = bookService.searchByTitle(USER_ID, "Dom");

        assertThat(resultado).isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════
    // filterByStatus — parametrizado com todos os status
    // ═══════════════════════════════════════════════════════════════════

    @ParameterizedTest(name = "deve filtrar por status {0}")
    @EnumSource(ReadingStatus.class)
    @DisplayName("deve filtrar corretamente por cada status de leitura — UC-003")
    void deveFiltrarPorStatus(ReadingStatus status) {
        bookService.createBook(livro("Livro A", "Autor", status), USER_ID);
        bookService.createBook(livro("Livro B", "Autor", ReadingStatus.WANT_TO_READ), OTHER_USER_ID);

        List<Book> resultado = bookService.filterByStatus(USER_ID, status);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getStatus()).isEqualTo(status);
        assertThat(resultado.get(0).getUserId()).isEqualTo(USER_ID);
    }

    // ═══════════════════════════════════════════════════════════════════
    // getBookById
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve retornar livro pelo id quando pertence ao usuário")
    void deveRetornarLivroPorId() {
        Book salvo = bookService.createBook(livro("Livro X", "Autor", ReadingStatus.READ), USER_ID);

        Optional<Book> resultado = bookService.getBookById(salvo.getId(), USER_ID);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(salvo.getId());
    }

    @Test
    @DisplayName("não deve retornar livro de outro usuário pelo id — isolamento RF004")
    void naoDeveRetornarLivroDeOutroUsuario() {
        Book salvo = bookService.createBook(livro("Livro X", "Autor", ReadingStatus.READ), OTHER_USER_ID);

        Optional<Book> resultado = bookService.getBookById(salvo.getId(), USER_ID);

        assertThat(resultado).isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════
    // updateBook
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve atualizar livro com novos dados — UC-002")
    void deveAtualizarLivro() {
        Book salvo = bookService.createBook(livro("Título Antigo", "Autor", ReadingStatus.WANT_TO_READ), USER_ID);

        Book novosDados = livro("Título Novo", "Novo Autor", ReadingStatus.READING);
        Optional<Book> atualizado = bookService.updateBook(salvo.getId(), novosDados, USER_ID);

        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().getTitle()).isEqualTo("Título Novo");
        assertThat(atualizado.get().getStatus()).isEqualTo(ReadingStatus.READING);
    }

    @Test
    @DisplayName("não deve atualizar livro de outro usuário — UC-002")
    void naoDeveAtualizarLivroDeOutroUsuario() {
        Book salvo = bookService.createBook(livro("Livro", "Autor", ReadingStatus.READ), OTHER_USER_ID);

        Optional<Book> resultado = bookService.updateBook(
                salvo.getId(), livro("Novo", "Novo", ReadingStatus.READING), USER_ID);

        assertThat(resultado).isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════════
    // deleteBook — caixa branca: testa cada branch do switch
    // ═══════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("deve deletar livro e retornar DELETED — UC-002")
    void deveDeletarLivro() {
        Book salvo = bookService.createBook(livro("Livro", "Autor", ReadingStatus.READ), USER_ID);

        DeleteResult resultado = bookService.deleteBook(salvo.getId(), USER_ID);

        assertThat(resultado).isEqualTo(DeleteResult.DELETED);
        assertThat(bookRepository.findById(salvo.getId())).isEmpty();
    }

    @Test
    @DisplayName("deve retornar NOT_FOUND quando livro não existe — UC-002")
    void deveRetornarNotFoundQuandoLivroNaoExiste() {
        DeleteResult resultado = bookService.deleteBook("id-inexistente", USER_ID);
        assertThat(resultado).isEqualTo(DeleteResult.NOT_FOUND);
    }

    @Test
    @DisplayName("deve retornar FORBIDDEN quando livro pertence a outro usuário — UC-002 E3")
    void deveRetornarForbiddenQuandoLivroDeOutroUsuario() {
        Book salvo = bookService.createBook(livro("Livro", "Autor", ReadingStatus.READ), OTHER_USER_ID);

        DeleteResult resultado = bookService.deleteBook(salvo.getId(), USER_ID);

        assertThat(resultado).isEqualTo(DeleteResult.FORBIDDEN);
        // Garante que o livro NÃO foi deletado
        assertThat(bookRepository.findById(salvo.getId())).isPresent();
    }

    // ═══════════════════════════════════════════════════════════════════
    // helper
    // ═══════════════════════════════════════════════════════════════════

    private Book livro(String title, String author, ReadingStatus status) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setGenre("Ficção");
        b.setStatus(status);
        return b;
    }
}