package ru.yandex.practicum;

import ru.yandex.practicum.exception.EmptyDictionaryException;
import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordNotFoundInDictionaryException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// Класс WordleDictionary для хранения и проверки словаря игры
public class WordleDictionary {
    // Поле для хранения длины игрового слова
    public static final int WORD_LENGTH = 5;
    // Поле для хранения регулярного выражения русских букв
    private static final String RUSSIAN_WORD_PATTERN = "[а-я]+";
    // Поле для хранения списка слов
    private final List<String> words;
    // Поле для быстрого поиска слова в словаре
    private final Set<String> wordSet;
    // Поле для выбора случайного слова
    private final Random random;

    // Конструктор класса для создания словаря из списка слов
    public WordleDictionary(List<String> sourceWords) throws EmptyDictionaryException {
        this(sourceWords, new Random());
    }

    // Конструктор класса для создания словаря с заданным генератором случайных чисел
    public WordleDictionary(List<String> sourceWords, Random random) throws EmptyDictionaryException {
        LinkedHashSet<String> normalizedWords = new LinkedHashSet<>();
        for (String word : sourceWords) {
            String normalizedWord = normalize(word);
            if (isCorrectWordForm(normalizedWord)) {
                normalizedWords.add(normalizedWord);
            }
        }
        if (normalizedWords.isEmpty()) {
            throw new EmptyDictionaryException("В словаре нет подходящих слов из пяти русских букв.");
        }
        this.words = new ArrayList<>(normalizedWords);
        this.wordSet = new HashSet<>(normalizedWords);
        this.random = random;
    }

    // Метод для нормализации слова под правила игры
    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    // Метод для проверки формы слова без обращения к словарю
    public static boolean isCorrectWordForm(String word) {
        return word != null && word.length() == WORD_LENGTH && word.matches(RUSSIAN_WORD_PATTERN);
    }

    // Метод для валидации слова игрока
    public String validateWord(String word) throws InvalidWordException, WordNotFoundInDictionaryException {
        String normalizedWord = normalize(word);
        if (normalizedWord.isBlank()) {
            throw new InvalidWordException("Введите слово или нажмите Enter для подсказки.");
        }
        if (normalizedWord.length() != WORD_LENGTH) {
            throw new InvalidWordException("Слово должно состоять ровно из пяти букв.");
        }
        if (!normalizedWord.matches(RUSSIAN_WORD_PATTERN)) {
            throw new InvalidWordException("Слово должно состоять только из русских букв.");
        }
        if (!contains(normalizedWord)) {
            throw new WordNotFoundInDictionaryException("Такого слова нет в словаре игры.");
        }
        return normalizedWord;
    }

    // Метод для проверки наличия слова в словаре
    public boolean contains(String word) {
        return wordSet.contains(normalize(word));
    }

    // Метод для получения случайного слова из словаря
    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    // Метод для получения неизменяемого списка слов
    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    // Метод для получения размера словаря
    public int size() {
        return words.size();
    }
}
