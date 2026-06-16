package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Класс WordleServerStatisticLoader для хранения результатов игроков в JSON-файле
public class WordleServerStatisticLoader {
    // Поле для хранения типа списка статистики для Gson
    private static final Type STATISTIC_LIST_TYPE = new TypeToken<List<GameStatistic>>() { }.getType();
    // Поле для сериализации и десериализации JSON
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // Поле для хранения пути к файлу статистики
    private final Path statisticPath;
    // Поле для логирования системных сообщений
    private final PrintWriter log;

    // Конструктор класса для создания загрузчика статистики
    public WordleServerStatisticLoader(Path statisticPath, PrintWriter log) {
        this.statisticPath = statisticPath;
        this.log = log;
    }

    // Метод для добавления новой победы и сохранения файла
    public synchronized void addWin(GameStatistic statistic) throws IOException {
        List<GameStatistic> statistics = loadAll();
        statistics.add(statistic);
        saveAll(statistics);
        log.println("Статистика сохранена для игрока: " + statistic.getNickname());
    }

    // Метод для загрузки всей статистики из JSON-файла
    public synchronized List<GameStatistic> loadAll() throws IOException {
        if (!Files.exists(statisticPath)) {
            return new ArrayList<>();
        }
        try (BufferedReader reader = Files.newBufferedReader(statisticPath, StandardCharsets.UTF_8)) {
            List<GameStatistic> statistics = gson.fromJson(reader, STATISTIC_LIST_TYPE);
            if (statistics == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(statistics);
        }
    }

    // Метод для сохранения всей статистики в JSON-файл
    private synchronized void saveAll(List<GameStatistic> statistics) throws IOException {
        Path parent = statisticPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(statisticPath, StandardCharsets.UTF_8)) {
            gson.toJson(statistics, writer);
        }
    }

    // Метод для получения рейтинга топ-10 и результата текущего игрока
    public synchronized List<PlayerRating> getTop(String currentPlayer) throws IOException {
        Map<String, List<GameStatistic>> byPlayer = new HashMap<>();
        for (GameStatistic statistic : loadAll()) {
            byPlayer.computeIfAbsent(statistic.getNickname(), key -> new ArrayList<>()).add(statistic);
        }
        List<PlayerRating> ratings = new ArrayList<>();
        for (Map.Entry<String, List<GameStatistic>> entry : byPlayer.entrySet()) {
            int wins = entry.getValue().size();
            int bestSteps = Integer.MAX_VALUE;
            for (GameStatistic statistic : entry.getValue()) {
                bestSteps = Math.min(bestSteps, statistic.getSteps());
            }
            ratings.add(new PlayerRating(entry.getKey(), wins, bestSteps, entry.getKey().equalsIgnoreCase(currentPlayer)));
        }
        ratings.sort(PlayerRating::compareTo);
        List<PlayerRating> result = new ArrayList<>();
        for (int i = 0; i < ratings.size() && i < 10; i++) {
            result.add(ratings.get(i));
        }
        addCurrentPlayerIfAbsent(currentPlayer, ratings, result);
        return result;
    }

    // Метод для добавления текущего игрока в ответ, если он не попал в топ-10
    private void addCurrentPlayerIfAbsent(String currentPlayer, List<PlayerRating> ratings, List<PlayerRating> result) {
        if (currentPlayer == null || currentPlayer.isBlank()) {
            return;
        }
        for (PlayerRating rating : result) {
            if (rating.getNickname().equalsIgnoreCase(currentPlayer)) {
                return;
            }
        }
        for (PlayerRating rating : ratings) {
            if (rating.getNickname().equalsIgnoreCase(currentPlayer)) {
                result.add(rating);
                return;
            }
        }
    }
}
