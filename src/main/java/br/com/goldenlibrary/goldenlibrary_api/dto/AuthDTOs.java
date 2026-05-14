package br.com.goldenlibrary.goldenlibrary_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTOs {

    public record RegisterRequest(
            @NotBlank(message = "Nome é obrigatorio")
            String nome,

            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "Formato de e-mail inválido")
            String email,

            @NotBlank(message = "Senha é obrigatoria")
            @Size(min = 6, message = "Senha deve ter no mínimo 6 carecteres")
            String password
    ) {}

    public record loginRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,
        @NotBlank(message = "Senha é obrigatória")
        String password
    ) {}

    public record RegisterResponse(
            String id,
            String name,
            String email,
            String role
    ) {}

    public record LoginResponse(
            String token,
            String name,
            String email
    ) {}

    public record ErrorResponse(
            String message
    ) {}


}
