package br.com.goldenlibrary.goldenlibrary_api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public abstract class MongoIntegrationTest {

    protected static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "goldenlibrary_test");
    }
}