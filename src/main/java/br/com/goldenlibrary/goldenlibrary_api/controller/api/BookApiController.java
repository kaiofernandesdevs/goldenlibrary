package br.com.goldenlibrary.goldenlibrary_api.controller.api;

import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
import br.com.goldenlibrary.goldenlibrary_api.service.BookService;
import br.com.goldenlibrary.goldenlibrary_api.service.BookService.DeleteResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ReadingStatus status) {

        String userId = user.getId();

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(bookService.searchByTitle(userId, title));
        }
        if (status != null) {
            return ResponseEntity.ok(bookService.filterByStatus(userId, status));
        }
        return ResponseEntity.ok(bookService.getAllBooks(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        return bookService.getBookById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @Valid @RequestBody Book book,
            @AuthenticationPrincipal User user) {

        Book saved = bookService.createBook(book, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable String id,
            @Valid @RequestBody Book book,
            @AuthenticationPrincipal User user) {

        return bookService.updateBook(id, book, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        return switch (bookService.deleteBook(id, user.getId())) {
            case DELETED   -> ResponseEntity.noContent().build();
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case FORBIDDEN -> ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        };
    }
}