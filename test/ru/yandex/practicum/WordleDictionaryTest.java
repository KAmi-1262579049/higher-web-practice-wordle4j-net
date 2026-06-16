package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.EmptyDictionaryException;
import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Класс WordleDictionaryTest для тестирования словаря Wordle
class WordleDictionaryTest {
    // Тест для проверки нормализации слов
    @Test
    void shouldNormalizeWord() {
        assertEquals("елка", WordleDictionary.normalize(" ЁЛКА "));
    }

    // Тест для проверки фильтрации словаря
    @Test
    void shouldKeepOnlyFiveLetterRussianWords() throws EmptyDictionaryException {
        WordleDictionary dictionary = new WordleDictionary(List.of("джава", "мир", "javaa", "кухня"));
        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("ДЖАВА"));
        assertTrue(dictionary.contains("кухня"));
    }

    // Тест для проверки ошибки пустого словаря
    @Test
    void shouldThrowWhenDictionaryHasNoValidWords() {
        assertThrows(EmptyDictionaryException.class, () -> new WordleDictionary(List.of("мир", "java")));
    }

    // Тест для проверки валидации длины слова
    @Test
    void shouldThrowWhenWordHasWrongLength() throws EmptyDictionaryException {
        WordleDictionary dictionary = new WordleDictionary(List.of("джава"));
        assertThrows(InvalidWordException.class, () -> dictionary.validateWord("мир"));
    }

    // Тест для проверки ошибки отсутствия слова в словаре
    @Test
    void shouldThrowWhenWordNotFound() throws EmptyDictionaryException {
        WordleDictionary dictionary = new WordleDictionary(List.of("джава"));
        assertThrows(WordNotFoundInDictionaryException.class, () -> dictionary.validateWord("кухня"));
    }
}
