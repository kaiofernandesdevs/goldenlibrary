package br.com.goldenlibrary.goldenlibrary_api.repository;

import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findByUserId(String userId);

    List<Book> findByUserIdAndStatus(String userId, ReadingStatus status);

    List<Book> findByUserIdAndTitleContainingIgnoreCase(String userId, String title);

    Optional<Book> findByIdAndUserId(String id, String userId);





}
