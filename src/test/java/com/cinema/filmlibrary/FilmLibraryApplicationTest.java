package com.cinema.filmlibrary;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilmLibraryApplicationTest {

	@Test
	void contextLoads(ApplicationContext context) {
		// Проверяем, что контекст приложения загружается
		assertThat(context).isNotNull();
	}

	@Test
	void mainMethodStartsApplication() {
		// Проверяем, что метод main запускает приложение без исключений
		FilmLibraryApplication.main(new String[] {});
	}
}