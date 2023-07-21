package com.example.library.controllers;

import com.example.library.models.Reader;
import com.example.library.services.ReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Slf4j
@Controller
public class ReaderController {

    private final ReaderService readerService;

    @Autowired
    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    // Метод для отображения страницы авторизации
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Вернуть имя шаблона для страницы авторизации
    }

    // Метод для обработки POST запроса на авторизацию
    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password) {
        // Здесь вы можете обработать логику авторизации, вызвав методы вашего сервиса для проверки учетных данных
        // Например, предположим, что у вас есть метод authenticateUser в вашем сервисе,
        // который возвращает true, если учетные данные верны, и false в противном случае.
        boolean isAuthenticated = readerService.authenticateReader(email, password);

        return "redirect:/"; // После успешного входа всегда перенаправляем на главную страницу

    }

    // Метод для отображения страницы регистрации
    @GetMapping("/registration")
    public String showRegistrationPage() {
        return "registration"; // Вернуть имя шаблона для страницы регистрации
    }

    // Метод для обработки POST запроса на регистрацию
    @PostMapping("/registration")
    public String register(@ModelAttribute Reader reader) {
        log.info("Received registration request for reader: {}", reader);
        boolean isSaved = readerService.createReader(reader);
        if (isSaved) {
            log.info("Reader successfully saved: {}", reader);
        } else {
            log.info("Reader with the same email already exists: {}", reader.getEmail());
        }
        return "redirect:/login";
    }



    @GetMapping("/readers")
    public String showReaders(Model model) {
        List<Reader> readers = readerService.getAllReaders();
        model.addAttribute("readers", readers);
        return "readers";
    }

    @GetMapping("/readers/{id}")
    public String showEditReaderForm(@PathVariable Long id, Model model) {
        Optional<Reader> optionalReader = readerService.getReaderById(id);
        if (optionalReader.isPresent()) {
            Reader reader = optionalReader.get();
            model.addAttribute("reader", reader);
            return "edit-readers";
        } else {
            // Обработка случая, когда читатель с указанным ID не найден
            return "redirect:/readers";
        }
    }

    @PostMapping("/readers/{id}")
    public String editReader(@PathVariable Long id, @ModelAttribute Reader updatedReader) {
        readerService.updateReader(id, updatedReader);
        return "redirect:/readers";
    }

    @PostMapping("/readers/archive/{id}")
    public ResponseEntity<String> archiveReader(@PathVariable Long id) {
        if(readerService.archiveReaderById(id)) {
            return ResponseEntity.ok("Читатель успешно архивирован");
        }
        else {
            return ResponseEntity.ok("Читатель крутой и его нельзя архивировать");
        }
        // Возвращаем ответ со статусом 200 OK

    }
}