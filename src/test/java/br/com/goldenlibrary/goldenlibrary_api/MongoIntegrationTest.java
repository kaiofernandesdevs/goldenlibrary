package br.com.goldenlibrary.goldenlibrary_api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class MongoIntegrationTest {

    // Inicialização static manual: o container sobe uma única vez para a suíte toda
    protected static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "goldenlibrary_test");
        registry.add("api.security.token.secret",
                () -> "dGVzdC1zZWNyZXQta2V5LWZvci1nb2xkZW5saWJyYXJ5LXRlc3RpbmctMTIz");
    }
}