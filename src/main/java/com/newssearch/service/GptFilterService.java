package com.newssearch.service;

import com.newssearch.controller.ConfigManager;
import com.newssearch.service.CSVservice.CsvDataProcessor;
import com.newssearch.service.CSVservice.CsvFileManager;
import com.newssearch.service.CSVservice.NewsDataParser;
import com.newssearch.service.CSVservice.NewsDataService;
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

    private static final String OUTPUT_FILE = "llm_results2.txt";
    private static final String OUTPUT_JSON_FILE = "llm_answer2.json";

    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();
        System.out.println(configManager.getOpenRouterApiKey());
        gptFiltering();
    }

    public static void gptFiltering() {
        try {

// 1. Отправка в LLM и вывод ответа

            String textToProcess = new String(Files.readAllBytes(Paths.get("MessagesForGPT.txt")), StandardCharsets.UTF_8);
            String systemPrompt = "Тебе нужно проанализировать новости и возвращать ответ строго в JSON-формате. " +
                    "Выбери новости, посвященые научно-технологическим достижениям, и отнеси их к конкретной группе тематик из следующего списка:" +
                    "\n" +
                    "1) Переход к передовым технологиям проектирования и создания высокотехнологичной продукции, основанным на применении интеллектуальных производственных решений, роботизированных и высокопроизводительных вычислительных систем, новых материалов и химических соединений, результатов обработки больших объемов данных, технологий машинного обучения и искусственного интеллекта. \n" +
                    "2) Переход к экологически чистой и ресурсосберегающей энергетике, повышение эффективности добычи и глубокой переработки углеводородного сырья, формирование новых источников энергии, способов её передачи и хранения. \n" +
                    "3) Переход к персонализированной, предиктивной и профилактической медицине, высокотехнологичному здравоохранению и технологиям здоровьесбережения, в том числе за счет рационального применения лекарственных препаратов (прежде всего антибактериальных) и использования генетических данных и технологий. \n" +
                    "4) Переход к высокопродуктивному и экологически чистому агро- и аквахозяйству, разработку и внедрение систем рационального применения средств химической и биологической защиты сельскохозяйственных растений и животных, хранение и эффективную переработку сельскохозяйственной продукции,создание безопасных и качественных, в том числе функциональных, продуктов питания. \n" +
                    "5) Противодействие техногенным, биогенным, социокультурным угрозам, терроризму и экстремистской идеологии, деструктивному иностранному информационно-психологическому воздействию, а также киберугрозам и иным источникам опасности для общества, экономики и государства, укрепление обороноспособности и национальной безопасности страны в условиях роста гибридных угроз. \n" +
                    "6) Повышение уровня связности территории Российской Федерации посредством создания интеллектуальных транспортных, энергетических и телекоммуникационных систем, а также занятия и удержания лидирующих позиций при создании международных транспортно-логистических систем, освоении и использовании космического пространства и воздушного пространства, океанов, Арктики и Антарктики. \n" +
                    "7) Возможность эффективного ответа российского общества на большие вызовы с учетом возрастающей актуальности синтетических научных дисциплин, созданных на стыке психологии, социологии, политологии, истории и научных исследований, связанных с этическими аспектами научно-технологического развития, изменениями социальных, политических и экономических отношений. \n" +
                    "8) Объективную оценка выбросов и поглощения климатически активных веществ, снижение их негативного воздействия на окружающую среду и климат, повышение возможности качественной адаптации экосистем, населения и отраслей экономики к климатическим изменениям. \n" +
                    "9) Переход к развитию природоподобных технологий, воспроизводящих системы и процессы живой природы в виде технических систем и технологических процессов, интегрированных в природную среду и естественный природный ресурсооборот. \n"
                    + "\n";

            String userPrompt = "Далее, блок новостей:\n" +
                    textToProcess +
                    "\nВерни JSON-объект со следующей структурой:\n" +
                    "{\n" +
                    "  \"name\": Название университета,\n" +
                    "  \"news_numbers\": [массив ссылок новостей],\n" +
                    "  \"news_texts\": [массив текстов этих новостей],\n" +
                    "  \"themes\": [массив индексов тематик из списка, к которым отнесены новости (от 1 до 9)]\n" +
                    "}\n\n" +
                    "Указания:\n" +
                    "1. Сохраняй соответствие между элементами массивов (первый номер - первый текст - первая тематика), а также пиши текст новостей полностью, не сокращай (но без индексов).\n" +
                    "2. Возвращай ТОЛЬКО валидный JSON, без дополнительного текста!";

            long startTime = System.currentTimeMillis();
            String llmResponse = askLLM(systemPrompt, userPrompt);
            long endTime = System.currentTimeMillis();
            System.out.println("Время выполнения запроса: " + (endTime - startTime) + " мс");

            System.out.println("Ответ от LLM:\n" + llmResponse);

// 2. Парсинг JSON
            JSONObject jsonResponse = new JSONObject(llmResponse);
            JSONArray newsNumbers = jsonResponse.getJSONArray("news_numbers");
            JSONArray newsTexts = jsonResponse.getJSONArray("news_texts");
            JSONArray themes = jsonResponse.getJSONArray("themes");

// 3. Вывод в консоль
            System.out.println("\nРезультаты парсинга:");
            System.out.println("Номера научных новостей: " + newsNumbers);
            System.out.println("Тексты новостей: " + newsTexts);
            System.out.println("Тематики: " + themes);

// 4. Сохранение в файл
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));
                 BufferedWriter writer2 = new BufferedWriter(new FileWriter(OUTPUT_JSON_FILE))) {

                writer.write("Номера научных новостей: " + newsNumbers + "\n");
                writer.write("Тексты новостей: " + newsTexts + "\n");
                writer.write("Тематики: " + themes + "\n");

                writer2.write(llmResponse);
            }


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
