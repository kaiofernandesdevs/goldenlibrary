package br.com.goldenlibrary.goldenlibrary_api.controller;

import br.com.goldenlibrary.goldenlibrary_api.security.CustomUserDetails;
import br.com.goldenlibrary.goldenlibrary_api.service.JwtService;
import br.com.goldenlibrary.goldenlibrary_api.service.UserService;
import br.com.goldenlibrary.goldenlibrary_api.service.UserService.EmailAlreadyExistsException;
import br.com.goldenlibrary.goldenlibrary_api.service.UserService.RegisterRequest;
import br.com.goldenlibrary.goldenlibrary_api.service.UserService.RegisterResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;

    public UserController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserService userService) {
        this.authManager = authManager;
        this.jwtService  = jwtService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request) {
        try {
            var user = userService.addNewUser(
                    new RegisterRequest(request.name(), request.email(), request.password())
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponse(user.getId(), user.getName(), user.getEmail()));
        } catch (EmailAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            String token = jwtService.newToken(user);

            return ResponseEntity.ok(new ResponseLogin(user.getName(), token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenciais inválidas"));
        }
    }

    public record SignupRequest(
            @NotBlank(message = "Nome é obrigatório") String name,
            @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres") String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "E-mail é obrigatório") String email,
            @NotBlank(message = "Senha é obrigatória") String password
    ) {}

    public record ResponseLogin(String name, String token) {}

    public record ErrorResponse(String message) {}
}