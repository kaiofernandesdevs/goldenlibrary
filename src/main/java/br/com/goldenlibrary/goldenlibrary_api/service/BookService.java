    package br.com.goldenlibrary.goldenlibrary_api.service;

    import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
    import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
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


        public List<Book> getAllBooks(String userId) {
            return bookRepository.findByUserId(userId);
        }

        public List<Book> searchByTitle(String userId, String title) {
            return bookRepository.findByUserIdAndTitleContainingIgnoreCase(userId, title);
        }

        public List<Book> filterByStatus(String userId, ReadingStatus status) {
            return bookRepository.findByUserIdAndStatus(userId, status);
        }

        public Optional<Book> getBookById(String id, String userId) {
            return bookRepository.findByIdAndUserId(id, userId);
        }

        public Book createBook(Book book, String userId) {
            book.setUserId(userId);
            book.setCreatedAt(LocalDateTime.now());
            book.setUpdatedAt(LocalDateTime.now());
            return bookRepository.save(book);
        }

        public Optional<Book> updateBook(String id, Book newData, String userId) {
            return bookRepository.findByIdAndUserId(id, userId)
                    .map(book -> {
                        book.setTitle(newData.getTitle());
                        book.setAuthor(newData.getAuthor());
                        book.setGenre(newData.getGenre());
                        book.setStatus(newData.getStatus());
                        book.setUpdatedAt(LocalDateTime.now());
                        return bookRepository.save(book);
                    });
        }

        public DeleteResult deleteBook(String id, String userId) {
            Optional<Book> book = bookRepository.findById(id);

            if (book.isEmpty()) {
                return DeleteResult.NOT_FOUND;
            }
            if (!book.get().getUserId().equals(userId)) {
                return DeleteResult.FORBIDDEN;
            }

            bookRepository.deleteById(id);
            return DeleteResult.DELETED;
        }

        public enum DeleteResult {
            DELETED, NOT_FOUND, FORBIDDEN
        }
    }