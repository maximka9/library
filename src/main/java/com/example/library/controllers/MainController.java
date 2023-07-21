package com.example.library.controllers;

import com.example.library.models.Reader;
import com.example.library.models.enums.Role;
import com.example.library.services.BookService;
import com.example.library.services.FormularyService;
import com.example.library.services.ReaderService;
import com.example.library.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
@Slf4j
@Controller
public class MainController {

    private final FormularyService formularyService;
    private final ReaderService readerService;
    private final BookService bookService;
    private final ReportService reportService;

    @Autowired
    public MainController(FormularyService formularyService, ReaderService readerService, BookService bookService, ReportService reportService) {
        this.formularyService = formularyService;
        this.readerService = readerService;
        this.bookService = bookService;
        this.reportService = reportService;
    }
    @GetMapping("/")
    public String showMainPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_[ROLE_ADMIN]"));

            if (isAdmin) {
                return "main"; // Перенаправляем админа на main.html
            } else {

                // Получаем ID текущего пользователя из аутентификации
                String username = authentication.getName();
                log.info(username);
                // Здесь вам нужно заменить getUserReaderIdByUsername() на ваш метод, который возвращает ID читателя по его имени пользователя
                Long readerId = getUserReaderIdByEmail(username);
                return "redirect:/user-books/" + readerId; // Перенаправляем обычного пользователя на user-books.html с передачей параметра readerId
            }
        }
        return "redirect:/login"; // Если пользователь не аутентифицирован, перенаправляем на страницу логина
    }

    // Добавьте метод для получения ID читателя по имени пользователя
    private Long getUserReaderIdByEmail(String email) {

        {
            Reader reader = readerService.getReaderByEmail(email);
            if (reader != null) {
                return reader.getId(); // Предполагаем, что у читателя есть поле id, которое хранит его уникальный идентификатор
            } else {
                // Если читатель с таким именем пользователя не найден, можно вернуть null или какое-то значение по умолчанию
                return null;
            }
        }
    }
}