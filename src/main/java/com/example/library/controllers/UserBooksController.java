package com.example.library.controllers;

import com.example.library.models.Book;
import com.example.library.models.Formulary;
import com.example.library.services.FormularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Controller
public class UserBooksController {

    private final FormularyService formularyService;

    @Autowired
    public UserBooksController(FormularyService formularyService) {
        this.formularyService = formularyService;
    }

    @GetMapping("/user-books/{readerId}")
    public String showUserBooks(@PathVariable Long readerId, Model model) {
        // Получаем все формуляры, связанные с читателем с заданным ID
        Set<Formulary> userFormularies = formularyService.getAllFormulariesByReader(readerId);

        // Получаем список книг, которые читатель взял, и которые находятся у него в данный момент
        List<Book> userBooks = formularyService.getAllBooksFromFormularies(userFormularies);

        model.addAttribute("userBooks", userBooks);
        return "user-books"; // The name of the Thymeleaf template (user-books.html)
    }
}