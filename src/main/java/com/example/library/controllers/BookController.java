package com.example.library.controllers;

import com.example.library.models.Book;
import com.example.library.models.Formulary;
import com.example.library.models.Reader;
import com.example.library.models.enums.Role;
import com.example.library.services.BookService;
import com.example.library.services.FormularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.library.services.ReportService;
import com.example.library.services.ReaderService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Slf4j
@Controller
public class BookController {

    private final BookService bookService;
    private final ReportService reportService;
    private final ReaderService readerService;
    private final FormularyService formularyService;

    @Autowired
    public BookController(BookService bookService, ReportService reportService, ReaderService readerService, FormularyService formularyService) {
        this.bookService = bookService;
        this.reportService = reportService;
        this.readerService = readerService;
        this.formularyService = formularyService;
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/books")
    public String createBook(@ModelAttribute Book newBook) {
        bookService.createBook(newBook);
        // После создания книги, перенаправляем пользователя на страницу со списком книг
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String showBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books"; // Возвращаем HTML-шаблон для всех пользователей (books.html)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/books/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = bookService.getBookById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            model.addAttribute("book", book);
            return "edit-books";
        } else {
            // Обработка случая, когда книга с указанным ID не найдена
            return "redirect:/books";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/books/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute Book updatedBook) {
        bookService.updateBook(id, updatedBook);
        return "redirect:/books";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/books/archive/{id}")
    public ResponseEntity<String> archiveBook(@PathVariable Long id) {
        bookService.archiveBookById(id);
        // Возвращаем ответ со статусом 200 OK
        return ResponseEntity.ok("Книга успешно архивирована");
    }

}