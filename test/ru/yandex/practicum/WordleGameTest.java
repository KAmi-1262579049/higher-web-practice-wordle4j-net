package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.GameException;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Класс WordleGameTest для тестирования игровой логики Wordle
class WordleGameTest {
    // Поле для хранения тестового словаря
    private WordleDictionary dictionary;
    // Поле для хранения тестового лога
    private PrintWriter log;

    // Метод для подготовки тестовых данных перед каждым тестом
    @BeforeEach
    void setUp() throws Exception {
        dictionary = new WordleDictionary(List.of("джава", "трава", "кухня", "ветер", "сапер", "бобер", "морда", "кулер"));
        log = new PrintWriter(System.out);
    }

    // Тест для проверки полного совпадения слова
    @Test
    void shouldReturnAllExactMatches() {
        assertEquals("+++++", WordleGame.analyzeGuess("джава", "джава"));
    }

    // Тест для проверки частичных совпадений букв
    @Test
    void shouldReturnCorrectWordleHint() {
        assertEquals("+^-^-", WordleGame.analyzeGuess("гонец", "герой"));
    }

    // Тест для проверки повторяющихся букв в подсказке
    @Test
    void shouldHandleRepeatedLettersCorrectly() {
        assertEquals("---++", WordleGame.analyzeGuess("трава", "джава"));
    }

    // Тест для проверки победы после правильного слова
    @Test
    void shouldWinAfterCorrectGuess() throws GameException {
        WordleGame game = new WordleGame(dictionary, "джава", log);
        GuessResult result = game.makeGuess("ДЖАВА");
        assertEquals("+++++", result.getHint());
        assertTrue(game.isWon());
        assertEquals(1, game.getUsedSteps());
    }

    // Тест для проверки уменьшения количества попыток только после корректного хода
    @Test
    void shouldDecreaseStepsAfterValidGuess() throws GameException {
        WordleGame game = new WordleGame(dictionary, "джава", log);
        game.makeGuess("трава");
        assertEquals(5, game.getStepsLeft());
        assertFalse(game.isFinished());
    }

    // Тест для проверки подсказки с учётом истории ходов
    @Test
    void shouldSuggestWordMatchingHistory() throws GameException {
        WordleGame game = new WordleGame(dictionary, "джава", log);
        game.makeGuess("трава");
        String suggestion = game.suggestWord();
        assertEquals("джава", suggestion);
        assertTrue(game.isUsedHints());
    }

    // Тест для проверки фильтрации кандидатов по истории ходов
    @Test
    void shouldFilterCandidatesByHistory() throws GameException {
        WordleGame game = new WordleGame(dictionary, "джава", log);
        game.makeGuess("трава");
        assertEquals(List.of("джава"), game.getAvailableCandidates());
    }
}
