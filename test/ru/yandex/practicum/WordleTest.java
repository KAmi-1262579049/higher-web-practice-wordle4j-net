package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Класс WordleTest для базового тестирования главного класса игры
class WordleTest {
    // Тест для проверки наличия главного класса приложения
    @Test
    void shouldCreateMainClassReference() {
        assertNotNull(Wordle.class);
    }
}
