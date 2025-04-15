package com.cinema.filmlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение CarDealer.
 * Этот класс запускает Spring Boot-приложение.
 */

@SpringBootApplication
public class FilmLibraryApplication {
    /**
    * Главный метод, запускающий приложение.
    * Инициализирует контекст Spring и запускает приложение.
    *
    * @param args аргументы командной строки, переданные при старте приложения.
    */
    public static void main(String[] args) {
        SpringApplication.run(FilmLibraryApplication.class, args);
    }
}