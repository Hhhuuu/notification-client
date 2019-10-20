package ru.mamapapa.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

/**
 * Хранение данных в файле, чтобы не отправлять данные повторно при запуске
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class Cache {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cache.class);
    private static final String APPLICATION_CACHE = "./cache";

    private Cache() {
    }

    /**
     * Сохранение приложений в кэш
     * @param applications - список приложений
     */
    public static void saveApplications(Map<String, String> applications) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(INDENT_OUTPUT);
            String applicationsJson = objectMapper.writeValueAsString(applications);
            save(applicationsJson, APPLICATION_CACHE);
        } catch (Exception e) {
            LOGGER.error("Не удалось сохранить данные в кэш!", e);
        }
    }

    /**
     * Получение из кэша данных
     * @return список приложений из кэша
     */
    public static Map<String, String> getApplicationsFromCache() {
        try {
            Path localPath = Paths.get(APPLICATION_CACHE);
            if (localPath.toFile().exists()) {
                try (BufferedReader reader = Files.newBufferedReader(localPath)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(reader, Map.class);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Не удалось получить данные из кэша!", e);
        }
        return new HashMap<>();
    }

    private static void save(String text, String fullFileName) {
        Path localPath = Paths.get(fullFileName);
        if (!localPath.toFile().exists()) {
            try {
                Files.createFile(localPath);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(fullFileName)) {
            byte[] buffer = text.getBytes(StandardCharsets.UTF_8);
            outputStream.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
