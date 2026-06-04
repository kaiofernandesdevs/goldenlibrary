package br.com.goldenlibrary.goldenlibrary_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GoldenlibraryApiApplicationTests extends MongoIntegrationTest {

	@Test
	void contextLoads() {
	}

}
