package com.newssearch.service;

import com.newssearch.model.MessageContainer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExcelService {
    public final Set<String> allWeeks = ConcurrentHashMap.newKeySet();
    String outputFile = "messagesExc.xlsx";
    private final List<String> headers = Arrays.asList(
            "University", "Title", "Date", "URL", "Text", "Expected priority"
    );
    private final List<Integer> columnWidths = Arrays.asList(
            15 * 256, 40 * 256, 10 * 256, 30 * 256, 70 * 256, 15 * 256
    );

    public void updateWeeklySheets(ConcurrentHashMap<String, List<MessageContainer>> groupedMessagesByWeek) throws IOException {
        Workbook workbook = openOrCreateWorkbook(outputFile);
        addMissingSheets(workbook, allWeeks);
        sortingAllSheets(workbook, allWeeks);


        CombinerNewsToOneArray.exportToFile(groupedMessagesByWeek, "MessagesForGPT.txt");



        updateWorkbookByWeekLists(workbook, groupedMessagesByWeek);

        saveWorkbook(workbook, outputFile);
        System.out.println("Excel-файл обновлён: " + outputFile);
    }

    public Workbook openOrCreateWorkbook(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return WorkbookFactory.create(fis);  // Открываем существующую книгу .xlsx
            }
        } else {
            return new XSSFWorkbook();
        }
    }

    public void addMissingSheets(Workbook wb, Set<String> allWeeks) {
        Set<String> existing = existingSheetNames(wb);
        for (String weekKey : allWeeks) {
            if (!existing.contains(weekKey)) {
                Sheet sheet = wb.createSheet(weekKey);

                /* Создание строки заголовков и установление ширины столбцов */
                Row headerRow = sheet.createRow(0);
                for (int col = 0; col < headers.size(); col++) {
                    sheet.setColumnWidth(col, columnWidths.get(col));
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(headers.get(col));
                }

            }
        }
    }

    public Set<String> existingSheetNames(Workbook wb) {
        Set<String> names = new HashSet<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            names.add(wb.getSheetName(i));
        }
        return names;
    }

    private void sortingAllSheets(Workbook wb, Set<String> unsorted){

        List<String> sortedList = new ArrayList<>(unsorted);

        sortedList.sort((a, b) -> {
            int indexOfDash = 4; int indexOf_W_PlusOne = 6;
            int yearA = Integer.parseInt(a.substring(0, indexOfDash));
            int weekA = Integer.parseInt(a.substring(indexOf_W_PlusOne));
            int yearB = Integer.parseInt(b.substring(0, indexOfDash));
            int weekB = Integer.parseInt(b.substring(indexOf_W_PlusOne));
            if (yearA != yearB) return Integer.compare(yearB, yearA);
            return Integer.compare(weekB, weekA);
        });
        for(int i = 0; i < sortedList.size();i++){
            wb.setSheetOrder(sortedList.get(i), i);
        }
    }

    public void saveWorkbook(Workbook wb, String path) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            wb.write(fos);  // Перезаписываем или создаём новый файл
        }
    }


    /**
     * Заполняет Workbook данными по неделям.
     *
     * @param workbook             Объект Excel Workbook.
     * @param groupedMessagesByWeek Мапа, где ключ — название листа (неделя), значение — список новостей в эту неделю.
     */
    public void updateWorkbookByWeekLists(Workbook workbook,
                                       ConcurrentHashMap<String, List<MessageContainer>> groupedMessagesByWeek) {

        groupedMessagesByWeek.forEach((weekKey, messages) -> {
            Sheet sheet = workbook.getSheet(weekKey);

            Set<String> existingLinks = collectExistingKeys(sheet);
            messages.removeIf(msg -> {
                String url = msg.getLink();
                return existingLinks.contains(url);
            });

            int rowNum = sheet.getLastRowNum() + 1;
            for (MessageContainer message : messages) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue("University");
                row.createCell(1).setCellValue(message.getTitle());
                row.createCell(2).setCellValue(message.getDate());
                row.createCell(3).setCellValue(message.getLink());
                row.createCell(4).setCellValue(message.getText());
                row.createCell(5).setCellValue("1 ... 9");

            }
        });
    }

    private Set<String> collectExistingKeys(Sheet sheet) {
        Set<String> existingUrls = new HashSet<>();
        int firstDataRow = sheet.getFirstRowNum() + 1;
        int lastRow = sheet.getLastRowNum();

        for (int i = firstDataRow; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell urlCell = row.getCell(getColumnIndexByHeader(sheet, "URL"));
            if (urlCell != null && urlCell.getCellType() == CellType.STRING) {
                String url = urlCell.getStringCellValue().trim();
                if (!url.isEmpty()) {
                    existingUrls.add(url);
                }
            }
        }
        return existingUrls;
    }
    private int getColumnIndexByHeader(Sheet sheet, String headerName) {
        Row headerRow = sheet.getRow(sheet.getFirstRowNum());

        for (Cell cell : headerRow) {
            if (cell.getCellType() == CellType.STRING &&
                    cell.getStringCellValue().equalsIgnoreCase(headerName)) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }



    public String parseSheetName(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(dateStr, formatter);

        // Определяем год и номер недели по ISO-8601
        WeekFields wf  = WeekFields.ISO;
        int yearBased  = date.get(wf.weekBasedYear());
        int weekOfYear = date.get(wf.weekOfWeekBasedYear());

        return String.format("%d-W%02d", yearBased, weekOfYear);
    }

    public void addWeek(String weekKey) {
        this.allWeeks.add(weekKey);
    }

}