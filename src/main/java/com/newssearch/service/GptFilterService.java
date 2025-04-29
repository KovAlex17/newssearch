package com.newssearch.service;

import com.newssearch.controller.ConfigManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GptFilterService {

    private static final String OPENROUTER_API_KEY = ConfigManager.getOpenRouterApiKey();
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "deepseek/deepseek-r1:free";

    private static final String MONGO_URI = ConfigManager.getMongoUri();
    private static final String DB_NAME = "priorities";
    private static final String COLLECTION_NAME = "NationalUniversities_nsu";

    private static final String OUTPUT_FILE = "llm_results.txt";

    public static void gptFiltering() {
        try {
            // 1. Подключение к MongoDB и получение документа
            //MongoClient mongoClient = MongoClients.create(MONGO_URI);
            //MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            //MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            /*String textToProcess = doc.getString("text");
            //System.out.println("Текст из MongoDB: \n" + textToProcess);*/


            // 2. Отправка в LLM и вывод ответа

            String textToProcess = new String(Files.readAllBytes(Paths.get("news_texts.txt")), StandardCharsets.UTF_8);
            String systemPrompt = "Ты должен анализировать новости и возвращать ответ в строгом JSON-формате. " +
                    "Выбери новости, посвященые научно-технологическим достижениям, которые можно " +
                    "отнести к одной или нескольким группам тематик из следующего списка:" +
                    "\n" +
                    "Умные фабрики, цифровые/виртуальные производственные потоки,\n" +
                    "Автоматизация процессов, расширенный контроль процессов,\n" +
                    "Цифровые двойники производственных линий, виртуальный ввод в эксплуатацию,\n" +
                    "Аддитивное производство (3D-печать) и гибридные процессы,\n" +
                    "Микро-/нанопроизводство и прецизионная инженерия,\n" +
                    "Оптимизация процессов, включая новые производственные процессы,\n" +
                    "Роботизированное оборудование, системы управления, планирование движения,\n" +
                    "Взаимодействие человека и робота, безопасность и эргономика,\n" +
                    "Мультикоординация роботов,\n" +
                    "Компьютерное моделирование (CFD, FEA, мультифизика)" +
                    "Архитектуры высокопроизводительных вычислений и подходы к параллельным вычислениям (графические процессоры, ПЛИС, облачные кластеры),\n" +
                    "Новые алгоритмы и аппаратное обеспечение квантовых вычислений,\n" +
                    "Разработка новых сплавов, композитов, полимеров, наноматериалов,\n" +
                    "Функциональные материалы (например, самовосстанавливающиеся, с памятью формы),\n" +
                    "Экологически чистые материалы и производство материалов с низким уровнем воздействия на окружающую среду,\n" +
                    "Хранение больших данных, распределенные вычисления и конвейеры передачи данных,\n" +
                    "Интеллектуальный анализ данных, потоковая аналитика и обработка данных в режиме реального времени,\n" +
                    "Передовые вычислительные архитектуры и управление данными Интернета вещей,\n" +
                    "Основные методы ML/AI (глубокое обучение, обучение с подкреплением, генеративные модели),\n" +
                    "Проектирование и оптимизация на основе ИИ (например, генеративный дизайн, обнаружение дефектов),\n" +
                    "Надежный искусственный интеллект (объяснимость, надежность, уменьшение предвзятости),\n" +
                    "Разработка и интеграция сенсоров (MEMS, оптические, биосенсоры и т.д.),\n" +
                    "Встраиваемый искусственный интеллект, передовая аналитика, аналитика в реальном времени,\n" +
                    "Разработка микроэлектроники и VLSI-дизайн (включая специализированные ускорители искусственного интеллекта),\n" +
                    "Безопасность киберфизических систем, обнаружение и предотвращение вторжений,\n" +
                    "Надежность техники и отказоустойчивые конструкции,\n" +
                    "Стандарты безопасности и сертификация (ISO/IEC и т.д.),\n" +
                    "Управление жизненным циклом продукции (PLM), системное проектирование на основе моделей,\n" +
                    "Подходы на основе цифровых нитей/цифровых двойников для связи проектирования, производства и эксплуатации,\n" +
                    "Стандарты взаимодействия, кроссплатформенная интеграция.\n";

            String userPrompt = "Далее, блок новостей (они пронумерованы):\n" +
                    textToProcess +
                    "\nВерни JSON-объект со следующей структурой:\n" +
                    "{\n" +
                    "  \"scientific_news\": [список номеров новостей о науке],\n" +
                    "  \"text\": \"тематика(тематики) из списка, к которой(которым) отнесена новость\"\n" +
                    "}\n\n" +
                    "Важно: возвращай ТОЛЬКО валидный JSON, без дополнительного текста!";

            String llmResponse = askLLM(systemPrompt, userPrompt);
            System.out.println("Ответ от LLM:\n" + llmResponse);

            // 3. Парсинг JSON
            JSONObject jsonResponse = new JSONObject(llmResponse);
            JSONArray scientificNews = jsonResponse.getJSONArray("scientific_news");
            String analysis = jsonResponse.getString("analysis");

            // 5. Вывод в консоль
            System.out.println("\nРезультаты парсинга:");
            System.out.println("Научные новости (номера): " + scientificNews);
            System.out.println("Анализ: " + analysis);

            // 6. Сохранение в файл
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
                writer.write("Научные новости (номера): " + scientificNews + "\n");
                writer.write("Анализ: " + analysis + "\n");
                System.out.println("\nРезультаты сохранены в файл: " + OUTPUT_FILE);
            }

            //mongoClient.close();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static String askLLM(String systemPrompt, String userPrompt) throws Exception {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(OPENROUTER_URL);

            // Заголовки (обязательные)
            request.setHeader("Authorization", "Bearer " + OPENROUTER_API_KEY);
            request.setHeader("Content-Type", "application/json; charset=UTF-8");


            // Тело запроса
            JSONObject payload = new JSONObject();
            payload.put("model", MODEL);
            //payload.put("temperature", 0.3); // Делаем ответы более детерминированными
            payload.put("response_format", new JSONObject().put("type", "json_object"));


            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
            payload.put("messages", messages);

            // Отправка запроса
            request.setEntity(new StringEntity(payload.toString(), StandardCharsets.UTF_8)); /* Надо указывать кодировку! */

            // Получение и обработка ответа
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("HTTP Status: " + statusCode);

            String rawResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            //System.out.println("Полный ответ API: " + rawResponse); // Для отладки

            JSONObject jsonResponse = new JSONObject(rawResponse);
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            // Очистка ответа от обратных кавычек и пометки json
            content = content.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();
            return content;
        }
    }
}
