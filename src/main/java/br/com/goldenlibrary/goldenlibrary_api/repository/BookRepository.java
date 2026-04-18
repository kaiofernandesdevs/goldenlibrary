package br.com.goldenlibrary.goldenlibrary_api.repository;

import br.com.goldenlibrary.goldenlibrary_api.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> {
}
