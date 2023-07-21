package com.example.library.controllers;

import com.example.library.models.Book;
import com.example.library.models.Formulary;
import com.example.library.models.Reader;
import com.example.library.services.BookService;
import com.example.library.services.FormularyService;
import com.example.library.services.ReaderService;
import com.example.library.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

@Controller
public class FormularyController {

    private final FormularyService formularyService;
    private final ReaderService readerService;
    private final BookService bookService;
    private final ReportService reportService;

    @Autowired
    public FormularyController(FormularyService formularyService, ReaderService readerService, BookService bookService, ReportService reportService) {
        this.formularyService = formularyService;
        this.readerService = readerService;
        this.bookService = bookService;
        this.reportService = reportService;
    }

    @GetMapping("/formulary")
    public String showFormularies(Model model) {
        List<Formulary> formularies = formularyService.getAllFormularies();
        model.addAttribute("formularies", formularies);
        return "formulary"; // The name of the Thymeleaf template (formulary.html)
    }

    // @PostMapping(value = "/formulary/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PostMapping("/formulary/new")
    public String createFormulary(@ModelAttribute Formulary newFormulary, RedirectAttributes redirectAttributes) {
        formularyService.createFormulary(newFormulary);
        redirectAttributes.addFlashAttribute("successMessage", "Формуляр успешно создан");

        try {
            YearMonth reportDate = YearMonth.now(); // или получите дату отчёта из другого источника
            reportService.updateReport(reportDate);
        } catch (IOException |  javax.mail.MessagingException e) {
            // Обработка ошибок при обновлении отчёта (если требуется)
            e.printStackTrace();
        }

        return "redirect:/formulary";
    }

    @GetMapping("/formulary/new")
    public String showCreateFormularyForm(Model model) {
        List<Reader> readers = readerService.getAllReaders();
        List<Book> books = bookService.getAllBooks();
        Formulary newFormulary = new Formulary();
        model.addAttribute("readers", readers);
        model.addAttribute("books", books);
        model.addAttribute("newFormulary", newFormulary);
        return "create-formulary"; // Название Thymeleaf шаблона для формы создания формуляра
    }

    @GetMapping("/formulary/{id}")
    public ResponseEntity<Formulary> getFormularyById(@PathVariable Long id) {
        Formulary formulary = formularyService.getFormularyById(id);
        if (formulary != null) {
            return new ResponseEntity<>(formulary, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/formulary/{id}")
    public ResponseEntity<Formulary> updateFormulary(@PathVariable Long id, @RequestBody Formulary updatedFormulary) {
        Formulary formulary = formularyService.updateFormulary(id, updatedFormulary);
        if (formulary != null) {
            return new ResponseEntity<>(formulary, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/formulary/{id}")
    public ResponseEntity<Void> deleteFormulary(@PathVariable Long id) {
        boolean deleted = formularyService.deleteFormulary(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/formulary/report")
    public ResponseEntity<byte[]> generateExcelReport() {
        List<Reader> readers = readerService.getAllReaders();
        List<Book> books = bookService.getAllBooks();
        List<Formulary> formularies = formularyService.getAllFormularies();
        YearMonth reportDate = YearMonth.now(); // Используем текущий месяц и год
        if (readers.isEmpty() || books.isEmpty() || formularies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        try {
            ByteArrayOutputStream excelReport = reportService.generateExcelReport(readers, formularies, reportDate);

            byte[] bytes = excelReport.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=report.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}