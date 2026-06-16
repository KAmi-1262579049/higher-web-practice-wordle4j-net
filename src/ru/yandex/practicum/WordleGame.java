package ru.yandex.practicum;

import ru.yandex.practicum.exception.GameException;
import ru.yandex.practicum.exception.InvalidWordException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Класс WordleGame для хранения состояния игры и игровой логики
public class WordleGame {
    // Поле для хранения максимального количества попыток
    public static final int MAX_STEPS = 6;
    // Поле для хранения символа точного совпадения
    public static final char EXACT_MATCH = '+';
    // Поле для хранения символа совпадения буквы на другой позиции
    public static final char WRONG_PLACE = '^';
    // Поле для хранения символа отсутствующей буквы
    public static final char NO_MATCH = '-';
    // Поле для хранения загаданного слова
    private final String answer;
    // Поле для хранения словаря игры
    private final WordleDictionary dictionary;
    // Поле для хранения лога программы
    private final PrintWriter log;
    // Поле для хранения истории попыток
    private final List<GuessResult> history = new ArrayList<>();
    // Поле для хранения уже предложенных подсказок
    private final Set<String> suggestedWords = new HashSet<>();
    // Поле для хранения оставшихся попыток
    private int stepsLeft = MAX_STEPS;
    // Поле для хранения признака победы
    private boolean won;
    // Поле для хранения признака использования подсказок
    private boolean usedHints;

    // Конструктор класса для создания игры со случайным ответом
    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary, dictionary.getRandomWord(), log);
    }

    // Конструктор класса для создания игры с заданным ответом
    public WordleGame(WordleDictionary dictionary, String answer, PrintWriter log) {
        this.dictionary = dictionary;
        this.answer = WordleDictionary.normalize(answer);
        this.log = log;
        if (!dictionary.contains(this.answer)) {
            throw new IllegalArgumentException("Загаданное слово должно быть в словаре.");
        }
        log.println("Создана новая игра. Ответ выбран, попыток: " + MAX_STEPS);
    }

    // Метод для выполнения хода игрока
    public GuessResult makeGuess(String input) throws GameException {
        if (isFinished()) {
            throw new InvalidWordException("Игра уже завершена.");
        }
        String word = dictionary.validateWord(input);
        String hint = analyzeGuess(word, answer);
        GuessResult result = new GuessResult(word, hint);
        history.add(result);
        stepsLeft--;
        won = word.equals(answer);
        log.println("Ход принят: " + word + ", подсказка: " + hint + ", осталось попыток: " + stepsLeft);
        return result;
    }

    // Метод для анализа слова по правилам Wordle
    public static String analyzeGuess(String guess, String answer) {
        String normalizedGuess = WordleDictionary.normalize(guess);
        String normalizedAnswer = WordleDictionary.normalize(answer);
        char[] result = new char[WordleDictionary.WORD_LENGTH];
        boolean[] usedAnswerLetters = new boolean[WordleDictionary.WORD_LENGTH];
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            if (normalizedGuess.charAt(i) == normalizedAnswer.charAt(i)) {
                result[i] = EXACT_MATCH;
                usedAnswerLetters[i] = true;
            } else {
                result[i] = NO_MATCH;
            }
        }
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            if (result[i] == EXACT_MATCH) {
                continue;
            }
            char guessLetter = normalizedGuess.charAt(i);
            for (int j = 0; j < WordleDictionary.WORD_LENGTH; j++) {
                if (!usedAnswerLetters[j] && guessLetter == normalizedAnswer.charAt(j)) {
                    result[i] = WRONG_PLACE;
                    usedAnswerLetters[j] = true;
                    break;
                }
            }
        }
        return new String(result);
    }

    // Метод для получения подсказки с учётом всей истории ходов
    public String suggestWord() throws InvalidWordException {
        usedHints = true;
        for (String word : dictionary.getWords()) {
            if (!suggestedWords.contains(word) && !wasAlreadyGuessed(word) && matchesHistory(word)) {
                suggestedWords.add(word);
                log.println("Выдана подсказка: " + word);
                return word;
            }
        }
        throw new InvalidWordException("Подходящих слов для подсказки не найдено.");
    }

    // Метод для проверки слова-кандидата по всем предыдущим подсказкам
    public boolean matchesHistory(String candidate) {
        for (GuessResult result : history) {
            String expectedHint = analyzeGuess(result.getWord(), candidate);
            if (!expectedHint.equals(result.getHint())) {
                return false;
            }
        }
        return true;
    }

    // Метод для получения всех подходящих слов-кандидатов
    public List<String> getAvailableCandidates() {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        for (String word : dictionary.getWords()) {
            if (!wasAlreadyGuessed(word) && matchesHistory(word)) {
                candidates.add(word);
            }
        }
        return new ArrayList<>(candidates);
    }

    // Метод для проверки, было ли слово уже введено игроком
    private boolean wasAlreadyGuessed(String word) {
        for (GuessResult result : history) {
            if (result.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }

    // Метод для проверки завершения игры
    public boolean isFinished() {
        return won || stepsLeft == 0;
    }

    // Метод для проверки победы игрока
    public boolean isWon() {
        return won;
    }

    // Метод для получения оставшихся попыток
    public int getStepsLeft() {
        return stepsLeft;
    }

    // Метод для получения использованных попыток
    public int getUsedSteps() {
        return MAX_STEPS - stepsLeft;
    }

    // Метод для получения загаданного слова
    public String getAnswer() {
        return answer;
    }

    // Метод для проверки использования подсказок
    public boolean isUsedHints() {
        return usedHints;
    }

    // Метод для получения истории ходов
    public List<GuessResult> getHistory() {
        return new ArrayList<>(history);
    }
}
