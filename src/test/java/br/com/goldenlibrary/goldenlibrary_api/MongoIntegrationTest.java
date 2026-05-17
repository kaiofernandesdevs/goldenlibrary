package br.com.goldenlibrary.goldenlibrary_api;


import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "goldenlibrary_test");
        // Chave JWT base64 para testes (256 bits)
        registry.add("api.security.token.secret",
                () -> "dGVzdC1zZWNyZXQta2V5LWZvci1nb2xkZW5saWJyYXJ5LXRlc3RpbmctMTIz");
    }




}
