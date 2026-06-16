package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Класс WordleDictionaryLoaderTest для тестирования загрузки словаря из файла
class WordleDictionaryLoaderTest {
    // Поле для хранения временной папки теста
    @TempDir
    Path tempDir;

    // Тест для проверки загрузки UTF-8 словаря
    @Test
    void shouldLoadDictionaryFromUtf8File() throws Exception {
        Path dictionaryPath = tempDir.resolve("dictionary.txt");
        Files.write(dictionaryPath, List.of("ЁЖИКИ", "мир", "джава"), StandardCharsets.UTF_8);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(new PrintWriter(System.out));
        WordleDictionary dictionary = loader.load(dictionaryPath);
        assertEquals(2, dictionary.size());
        assertTrue(dictionary.contains("ежики"));
        assertTrue(dictionary.contains("джава"));
    }
}
