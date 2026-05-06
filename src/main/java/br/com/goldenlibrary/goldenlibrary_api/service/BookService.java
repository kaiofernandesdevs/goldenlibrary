package br.com.goldenlibrary.goldenlibrary_api.service;

import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import br.com.goldenlibrary.goldenlibrary_api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }

    public Optional<Book> updateBook(String id, Book newData) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(newData.getTitle());
            book.setAuthor(newData.getAuthor());
            book.setGenre(newData.getGenre());
            book.setUpdatedAt(LocalDateTime.now());
            return bookRepository.save(book);
        });
    }

    public boolean deleteBook(String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            return false;
        }
        bookRepository.deleteById(id);
        return true;
    }
}