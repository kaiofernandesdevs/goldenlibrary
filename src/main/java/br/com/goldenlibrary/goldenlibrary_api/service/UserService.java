package br.com.goldenlibrary.goldenlibrary_api.service;

import br.com.goldenlibrary.goldenlibrary_api.entity.User;
import br.com.goldenlibrary.goldenlibrary_api.enums.UserRole;
import br.com.goldenlibrary.goldenlibrary_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User addNewUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("E-mail passado já está cadastrado");
        }

        User newUser = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserRole.USER
        );

        return userRepository.save(newUser);
    }

    public record RegisterRequest(String name, String email, String password) {}

    public record RegisterResponse(String id, String name, String email) {}

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}