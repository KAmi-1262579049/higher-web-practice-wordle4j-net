package ru.yandex.practicum;

import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.exception.EmptyDictionaryException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Класс WordleDictionaryLoader для загрузки словаря из файла
public class WordleDictionaryLoader {
    // Поле для логирования системных сообщений
    private final PrintWriter log;

    // Конструктор класса для создания загрузчика словаря
    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    // Метод для загрузки словаря из файла в кодировке UTF-8
    public WordleDictionary load(Path dictionaryPath) throws DictionaryLoadException, EmptyDictionaryException {
        List<String> words = new ArrayList<>();
        log.println("Загрузка словаря из файла: " + dictionaryPath);
        try (BufferedReader reader = Files.newBufferedReader(dictionaryPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException exception) {
            log.println("Ошибка загрузки словаря: " + exception.getMessage());
            throw new DictionaryLoadException("Не удалось загрузить словарь.", exception);
        }
        WordleDictionary dictionary = new WordleDictionary(words);
        log.println("Словарь успешно загружен. Подходящих слов: " + dictionary.size());
        return dictionary;
    }
}
