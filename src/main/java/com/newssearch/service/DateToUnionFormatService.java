package com.newssearch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

public class DateToUnionFormatService {
    private static final Locale RU_LOCALE = new Locale("ru", "RU");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("d.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yy"),
            DateTimeFormatter.ofPattern("d.MM.yy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yy"),
            DateTimeFormatter.ofPattern("d/MM/yy"),
            // Русские месяцы
            DateTimeFormatter.ofPattern("dd MMMM yyyy", RU_LOCALE),
            DateTimeFormatter.ofPattern("d MMMM yyyy", RU_LOCALE),
            DateTimeFormatter.ofPattern("dd MMMM", RU_LOCALE),
            DateTimeFormatter.ofPattern("d MMMM", RU_LOCALE),
            DateTimeFormatter.ofPattern("dd MMM yyyy", RU_LOCALE),
            DateTimeFormatter.ofPattern("d MMM yyyy", RU_LOCALE),
            // Форматы с временем
            DateTimeFormatter.ofPattern("E, dd/MM/yyyy - HH:mm", RU_LOCALE),
            DateTimeFormatter.ofPattern("d MMMM yyyy | HH:mm", RU_LOCALE)
    };

    private static final Map<String, String> monthMap = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );


    private String preprocessDate(String dateString) {
        String processed = dateString.trim().toLowerCase().replaceAll("\\s+", " ");

        if (!processed.matches(".*\\d{4}.*")) processed += " " + LocalDate.now().getYear(); // Добавили год если не было

        processed = processed.replaceAll(
                "(\\d{1,2})\\s*([а-яё]{3,})\\s*(\\d{4})(.*)", // Разделили пробелами части даты
                "$1 $2 $3"
        );

        processed = replaceShortRussianMonthWithNumber(processed); // Заменили неполные русские месяцы на цифры
        return processed;
    }

    private String replaceShortRussianMonthWithNumber(String input) {
        for (Map.Entry<String, String> entry : monthMap.entrySet()) {
            input = input.replaceAll("\\s" + entry.getKey() + "\\s", "." + entry.getValue() + ".");
        }
        return input;
    }
    public LocalDate parseDate(String dateString) {
        String processedDate = preprocessDate(dateString);

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(processedDate, formatter);
            } catch (DateTimeParseException e) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(processedDate, formatter);
                    return dateTime.toLocalDate();
                } catch (DateTimeParseException e2) {
                    // Продолжаем со следующим форматом
                }
            }
        }

        throw new IllegalArgumentException("Не удалось распарсить дату: " + dateString);
    }

    public String normalizeDate(String dateString) {
        LocalDate date = parseDate(dateString);
        return date.format(OUTPUT_FORMAT);
    }
}
