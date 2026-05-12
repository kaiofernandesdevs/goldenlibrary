package br.com.goldenlibrary.goldenlibrary_api.repository;

import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
