package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

// Класс WordleServer для запуска HTTP-сервера статистики Wordle
public class WordleServer {
    // Поле для хранения порта сервера
    private static final int PORT = 8080;
    // Поле для хранения пути к файлу статистики
    private static final Path STATISTIC_PATH = Path.of("wordle-statistics.json");
    // Поле для сериализации и десериализации JSON
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Поле для хранения загрузчика статистики
    private final WordleServerStatisticLoader statisticLoader;
    // Поле для хранения HTTP-сервера
    private final HttpServer server;
    // Поле для логирования системных сообщений
    private final PrintWriter log;

    // Конструктор класса для создания сервера статистики
    public WordleServer(WordleServerStatisticLoader statisticLoader, PrintWriter log, int port) throws IOException {
        this.statisticLoader = statisticLoader;
        this.log = log;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/stats", this::handleStats);
    }

    // Метод для запуска сервера
    public void start() {
        server.start();
        log.println("Сервер статистики запущен.");
    }

    // Метод для остановки сервера
    public void stop() {
        server.stop(0);
        log.println("Сервер статистики остановлен.");
    }

    // Метод для обработки запросов статистики
    private void handleStats(HttpExchange exchange) throws IOException {
        try {
            if ("POST".equals(exchange.getRequestMethod())) {
                handlePost(exchange);
            } else if ("GET".equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else {
                sendJson(exchange, 405, Collections.singletonMap("error", "Метод не поддерживается."));
            }
        } catch (IOException exception) {
            log.println("Ошибка обработки HTTP-запроса: " + exception.getMessage());
            sendJson(exchange, 500, Collections.singletonMap("error", "Внутренняя ошибка сервера."));
        } finally {
            exchange.close();
        }
    }

    // Метод для обработки POST-запроса сохранения победы
    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            GameStatistic statistic = GSON.fromJson(reader, GameStatistic.class);
            if (statistic == null || statistic.getNickname() == null || statistic.getNickname().isBlank()) {
                sendJson(exchange, 400, Collections.singletonMap("error", "Никнейм обязателен."));
                return;
            }
            statisticLoader.addWin(statistic);
            sendJson(exchange, 201, Collections.singletonMap("status", "saved"));
        }
    }

    // Метод для обработки GET-запроса рейтинга игроков
    private void handleGet(HttpExchange exchange) throws IOException {
        String player = getQueryParameter(exchange.getRequestURI(), "player");
        List<PlayerRating> rating = statisticLoader.getTop(player);
        sendJson(exchange, 200, rating);
    }

    // Метод для получения параметра из query-строки
    private String getQueryParameter(URI uri, String name) {
        String query = uri.getQuery();
        if (query == null || query.isBlank()) {
            return "";
        }
        String[] parts = query.split("&");
        for (String part : parts) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2 && pair[0].equals(name)) {
                return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    // Метод для отправки JSON-ответа клиенту
    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] response = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    // Метод для запуска приложения-сервера
    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter("wordle-server.log", StandardCharsets.UTF_8)) {
            WordleServerStatisticLoader loader = new WordleServerStatisticLoader(STATISTIC_PATH, log);
            WordleServer wordleServer = new WordleServer(loader, log, PORT);
            wordleServer.start();
            System.out.println("WordleServer запущен на http://localhost:" + PORT + "/stats");
            System.out.println("Нажмите Enter для остановки сервера.");
            System.in.read();
            wordleServer.stop();
        } catch (IOException exception) {
            System.err.println("Не удалось запустить сервер: " + exception.getMessage());
        }
    }
}
