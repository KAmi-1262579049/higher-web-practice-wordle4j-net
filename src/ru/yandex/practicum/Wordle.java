package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.exception.GameException;
import ru.yandex.practicum.exception.ProgramException;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

// Класс Wordle для запуска консольной игры и общения с игроком
public class Wordle {
    // Поле для хранения пути к словарю
    private static final Path DICTIONARY_PATH = Path.of("words_ru.txt");
    // Поле для хранения пути к лог-файлу
    private static final String LOG_FILE = "wordle.log";
    // Поле для хранения адреса сервера статистики
    private static final String SERVER_URL = "http://localhost:8080/stats";
    // Поле для хранения типа списка рейтинга для Gson
    private static final Type RATING_LIST_TYPE = new TypeToken<List<PlayerRating>>() { }.getType();
    // Поле для сериализации и десериализации JSON
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Метод для запуска приложения игры
    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(LOG_FILE, StandardCharsets.UTF_8);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            runGame(scanner, log);
        } catch (ProgramException exception) {
            System.out.println("Произошла системная ошибка. Подробности записаны в лог.");
        } catch (IOException exception) {
            System.out.println("Не удалось создать лог-файл: " + exception.getMessage());
        } catch (RuntimeException exception) {
            System.out.println("Произошла непредвиденная ошибка. Проверьте лог-файл.");
        }
    }

    // Метод для подготовки словаря и запуска игрового цикла
    private static void runGame(Scanner scanner, PrintWriter log) throws ProgramException {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(DICTIONARY_PATH);
        WordleGame game = new WordleGame(dictionary, log);
        System.out.println("Игра Wordle началась. Нужно угадать русское слово из 5 букв за 6 попыток.");
        System.out.println("Введите слово или нажмите Enter, чтобы получить подсказку.");
        while (!game.isFinished()) {
            System.out.print("Осталось попыток: " + game.getStepsLeft() + ". Ваш ход: ");
            String input = scanner.nextLine();
            if (input.isBlank()) {
                showSuggestion(game);
                continue;
            }
            makePlayerMove(game, input);
        }
        finishGame(scanner, game, log);
    }

    // Метод для вывода подсказки игроку
    private static void showSuggestion(WordleGame game) {
        try {
            String suggestion = game.suggestWord();
            System.out.println("Подсказка: попробуйте слово \"" + suggestion + "\".");
        } catch (GameException exception) {
            System.out.println(exception.getMessage());
        }
    }

    // Метод для выполнения хода игрока и вывода результата
    private static void makePlayerMove(WordleGame game, String input) {
        try {
            GuessResult result = game.makeGuess(input);
            System.out.println(result.getWord());
            System.out.println(result.getHint());
        } catch (GameException exception) {
            System.out.println(exception.getMessage());
        }
    }

    // Метод для завершения игры и отправки статистики при победе
    private static void finishGame(Scanner scanner, WordleGame game, PrintWriter log) {
        if (game.isWon()) {
            System.out.println("Победа! Вы угадали слово за " + game.getUsedSteps() + " попыток.");
            sendStatisticAfterWin(scanner, game, log);
        } else {
            System.out.println("Попытки закончились. Загаданное слово: " + game.getAnswer());
        }
    }

    // Метод для отправки статистики на сервер после победы
    private static void sendStatisticAfterWin(Scanner scanner, WordleGame game, PrintWriter log) {
        System.out.print("Введите никнейм для рейтинга: ");
        String nickname = scanner.nextLine().trim();
        if (nickname.isBlank()) {
            System.out.println("Никнейм пустой, статистика не отправлена.");
            return;
        }
        try {
            HttpClient client = HttpClient.newHttpClient();
            GameStatistic statistic = new GameStatistic(nickname, game.getUsedSteps(), game.isUsedHints());
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(statistic), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (postResponse.statusCode() >= 200 && postResponse.statusCode() < 300) {
                System.out.println("Статистика отправлена.");
                printRating(client, nickname);
            } else {
                System.out.println("Сервер не принял статистику. Код: " + postResponse.statusCode());
            }
        } catch (IOException exception) {
            log.println("Ошибка отправки статистики: " + exception.getMessage());
            System.out.println("Не удалось подключиться к серверу статистики.");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.println("Отправка статистики была прервана: " + exception.getMessage());
            System.out.println("Отправка статистики была прервана.");
        }
    }

    // Метод для загрузки и вывода рейтинга игроков
    private static void printRating(HttpClient client, String nickname) throws IOException, InterruptedException {
        String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "?player=" + encodedNickname))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (getResponse.statusCode() != 200) {
            System.out.println("Не удалось получить рейтинг. Код: " + getResponse.statusCode());
            return;
        }
        List<PlayerRating> rating = GSON.fromJson(getResponse.body(), RATING_LIST_TYPE);
        System.out.println("Топ игроков:");
        for (int i = 0; i < rating.size(); i++) {
            PlayerRating player = rating.get(i);
            StringBuilder line = new StringBuilder();
            line.append(i + 1).append(". ")
                    .append(player.getNickname())
                    .append(" - побед: ").append(player.getWins())
                    .append(", лучший результат: ").append(player.getBestSteps());
            if (player.isCurrentPlayer()) {
                line.append(" <- вы");
            }
            System.out.println(line);
        }
    }
}
