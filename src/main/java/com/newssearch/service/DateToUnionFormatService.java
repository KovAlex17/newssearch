package com.newssearch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.regex.Pattern;

public class DateToUnionFormatService {

    private static final Locale RU_LOCALE = new Locale("ru", "RU");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Основные форматы для парсинга дат
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

    // Предобработка строки даты
    private static String preprocessDate(String dateString) {
        String processed = dateString.trim().replaceAll("\\s+", " ");
        // Обработка случаев без пробелов между числом и месяцем
        processed = processed.replaceAll("(\\d+)(января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)", "$1 $2");
        // Обработка сокращений типа "10Июн2025"
        processed = processed.replaceAll("(\\d+)(Июн|июн|Май|май|Дек|дек)", "$1 $2");
        return processed;
    }

    // Парсинг даты
    public static LocalDate parseDate(String dateString) {
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

        // Обработка случаев типа "25декабря"
        Pattern noSpacePattern = Pattern.compile("(\\d{1,2})(января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)", Pattern.CASE_INSENSITIVE);
        if (noSpacePattern.matcher(processedDate.toLowerCase()).find()) {
            String normalized = processedDate.toLowerCase().replaceAll("(\\d{1,2})(января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)", "$1 $2");
            try {
                return LocalDate.parse(normalized,
                        new DateTimeFormatterBuilder()
                                .appendPattern("d MMMM")
                                .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
                                .toFormatter(RU_LOCALE));
            } catch (DateTimeParseException e) {
                // Продолжаем
            }
        }

        throw new IllegalArgumentException("Не удалось распарсить дату: " + dateString);
    }

    // Приведение к единому формату
    public static String normalizeDate(String dateString) {
        LocalDate date = parseDate(dateString);
        return date.format(OUTPUT_FORMAT);
    }

    // Тестирование
    public static void main(String[] args) {
        String[] testDates = {
                "01.04.2025",
                "16 июня",
                "11.06.2025",
                "10Июн2025",
                "23.05.2025",
                "09 июня 2025",
                "25декабря",
                "11 июня",
                "11.06.2025",
                "11 июн 2025",
                "11 июня 2025г.",
                "26.05.2025",
                "10 июня 2025",
                "02.06.2025",
                "30 мая",
                "09Июн 2025",
                "16.06.2025",
                "17 июня 2025",
                "14/06/2018",
                "10.06.2025",
                "11 Июн 2025",
                "28 Мая 2025",
                "ср, 11/06/2025 - 17:01",
                "6 июня 2025 | 12:45",
                "09.06.2025"
        };

        System.out.println("Тестирование парсинга дат:");
        System.out.println("=".repeat(60));

        for (String dateStr : testDates) {
            try {
                String normalized = normalizeDate(dateStr);
                System.out.printf("%-30s -> %s%n", dateStr, normalized);
            } catch (Exception e) {
                System.out.printf("%-30s -> ОШИБКА: %s%n", dateStr, e.getMessage());
            }
        }
    }
}
