package br.com.goldenlibrary.goldenlibrary_api.tokensjwt;

import br.com.goldenlibrary.goldenlibrary_api.security.CustomUserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final int expirationInterval = 10;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String newToken(CustomUserDetails user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationInterval, ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("goldenlibrary")
                .issuedAt(now)
                .expiresAt(expiration)
                .subject(user.getUsername())
                .claim("name", user.getName())
                .claim("id", user.getId())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        try {
            return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        } catch (JwtEncodingException ex) {
            throw new RuntimeException("Erro crítico ao codificar o token JWT", ex);
        }
    }

    private Jwt decodedToken(String token) {
        return this.jwtDecoder.decode(token);
    }


    public CustomUserDetails getUSerInToken(String token) {
        try {
            Jwt jwt = decodedToken(token);

            String username = jwt.getSubject();
            String name = jwt.getClaimAsString("name");
            String id = jwt.getClaimAsString("id");

            return new CustomUserDetails(id, name, username);
        } catch (JwtException ex) {
            throw ex;
        }
    }
}
