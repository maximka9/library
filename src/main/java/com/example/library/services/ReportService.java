package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.models.Formulary;
import com.example.library.models.Reader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.mail.MessagingException;
@Service
public class ReportService {
    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @Autowired
    private FormularyService formularyService;

    @Autowired
    private EmailService emailService;

    @Value("${penalty.rate}")
    private double penaltyRate; // Наложенная пеня за просрочку (устанавливается в application.properties)

    public ByteArrayOutputStream generateExcelReport(List<Reader> readers, List<Formulary> formularies, YearMonth reportDate) throws IOException, MessagingException, javax.mail.MessagingException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчет");

        // Создаем стили для заголовков и данных
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        CellStyle dataStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        dataStyle.setDataFormat(dataFormat.getFormat("dd.MM.yyyy"));

        // Создаем заголовки
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID пользователя");
        headerRow.createCell(1).setCellValue("Имя пользователя");
        headerRow.createCell(2).setCellValue("Email пользователя");
        headerRow.createCell(3).setCellValue("ID книги");
        headerRow.createCell(4).setCellValue("Название книги");
        headerRow.createCell(5).setCellValue("Автор книги");
        headerRow.createCell(6).setCellValue("Год издания книги");
        headerRow.createCell(7).setCellValue("Дата возврата книги");
        headerRow.createCell(8).setCellValue("Количество просроченных дней");

        // Заполняем данными
        int rowNum = 1;
        for (Reader reader : readers) {
            for (Formulary formulary : formularies) {
                if (formulary.getReader().equals(reader) && isSameMonthYear(formulary.getReturnDate(), reportDate)) {
                    Book book = formulary.getBook();
                    Row dataRow = sheet.createRow(rowNum++);
                    dataRow.createCell(0).setCellValue(reader.getId());
                    dataRow.createCell(1).setCellValue(reader.getName());
                    dataRow.createCell(2).setCellValue(reader.getEmail());
                    dataRow.createCell(3).setCellValue(book.getId());
                    dataRow.createCell(4).setCellValue(book.getTitle());
                    dataRow.createCell(5).setCellValue(book.getAuthor());
                    dataRow.createCell(6).setCellValue(book.getYear());
                    Cell dueDateCell = dataRow.createCell(7);
                    dueDateCell.setCellValue(formulary.getReturnDate());
                    dueDateCell.setCellStyle(dataStyle);

                    LocalDate returnDate = formulary.getReturnDate();
                    long overdueDays = calculateDaysOverdue(returnDate);
                    dataRow.createCell(8).setCellValue(overdueDays);

                    if (overdueDays > 0) {
                        double penaltyAmount = overdueDays * penaltyRate;
                        String subject = "Просрочка возврата книги";
                        String content = "Уважаемый(ая) " + reader.getName() + ",\n\n"
                                + "Вы не вернули книгу \"" + book.getTitle() + "\", которую должны были вернуть "
                                + "до " + formulary.getReturnDate() + ".\n"
                                + "Общее количество просроченных дней: " + overdueDays + "\n"
                                + "Пеня за просрочку: " + penaltyAmount + " рублей.\n"
                                + "Пожалуйста, верните книгу как можно скорее, чтобы избежать дополнительных штрафов.\n\n"
                                + "С уважением,\n"
                                + "Библиотека";
                        emailService.sendEmail(reader.getEmail(), subject, content);
                    }
                }
            }
        }

        // Автоматически подгоняем ширину столбцов
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Переводим workbook в массив байтов и создаем ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream;
    }

    public void updateReport(YearMonth reportDate) throws IOException, MessagingException, javax.mail.MessagingException {
        List<Reader> readers = readerService.getAllReaders();
        List<Formulary> formularies = formularyService.getAllFormularies();

        // Генерируем отчет
        ByteArrayOutputStream excelReport = generateExcelReport(readers, formularies, reportDate);

        // Сохраняем отчет в файл
        File outputFile = new File("C:\\Users\\improver1\\Desktop\\report.xlsx");
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            excelReport.writeTo(fileOutputStream);
        }

        // Закрываем ByteArrayOutputStream
        excelReport.close();
    }

    public long calculateDaysOverdue(LocalDate returnDate) {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isAfter(returnDate)) {
            return ChronoUnit.DAYS.between(returnDate, currentDate);
        } else {
            return 0;
        }
    }

    private boolean isSameMonthYear(LocalDate date1, YearMonth date2) {
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth();
    }
}