package br.com.goldenlibrary.goldenlibrary_api.controller;

import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
import br.com.goldenlibrary.goldenlibrary_api.security.CustomUserDetails;
import br.com.goldenlibrary.goldenlibrary_api.service.BookService;
import br.com.goldenlibrary.goldenlibrary_api.service.BookService.DeleteResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> listBooks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String title) {

        String userId = userDetails.getId();

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(bookService.searchByTitle(userId, title));
        }
        return ResponseEntity.ok(bookService.getAllBooks(userId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterByStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam ReadingStatus status) {

        return ResponseEntity.ok(bookService.filterByStatus(userDetails.getId(), status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return bookService.getBookById(id, userDetails.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestBody @Valid Book book,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Book created = bookService.createBook(book, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable String id,
            @RequestBody @Valid Book newData,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return bookService.updateBook(id, newData, userDetails.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        DeleteResult result = bookService.deleteBook(id, userDetails.getId());

        return switch (result) {
            case DELETED -> ResponseEntity.noContent().build();
            case NOT_FOUND -> ResponseEntity.notFound().build();
            case FORBIDDEN -> ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        };
    }
}