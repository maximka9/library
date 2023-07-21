package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.models.Formulary;
import com.example.library.models.Reader;
import com.example.library.repositories.FormularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class FormularyService {

    private final FormularyRepository formularyRepository;

    @Autowired
    public FormularyService(FormularyRepository formularyRepository) {
        this.formularyRepository = formularyRepository;
    }

    public Formulary createFormulary(Formulary formulary) {
        return formularyRepository.save(formulary);
    }

    public Formulary getFormularyById(Long id) {
        Optional<Formulary> optionalFormulary = formularyRepository.findById(id);
        return optionalFormulary.orElse(null);
    }

    public List<Formulary> getAllFormularies() {
        return formularyRepository.findAll();
    }

    public Formulary updateFormulary(Long id, Formulary updatedFormulary) {
        Optional<Formulary> optionalFormulary = formularyRepository.findById(id);
        if (optionalFormulary.isPresent()) {
            Formulary existingFormulary = optionalFormulary.get();
            existingFormulary.setReader(updatedFormulary.getReader());
            existingFormulary.setBook(updatedFormulary.getBook());
            existingFormulary.setIssuedDate(updatedFormulary.getIssuedDate());
            existingFormulary.setReturnDate(updatedFormulary.getReturnDate());
            existingFormulary.setArchived(updatedFormulary.isArchived());
            return formularyRepository.save(existingFormulary);
        } else {
            return null;
        }
    }

    public boolean deleteFormulary(Long id) {
        Optional<Formulary> optionalFormulary = formularyRepository.findById(id);
        if (optionalFormulary.isPresent()) {
            formularyRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public long calculateDaysOverdue(Formulary formulary) {
        LocalDate currentDate = LocalDate.now();
        LocalDate dueDate = formulary.getReturnDate();
        if (currentDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, currentDate);
        } else {
            return 0;
        }
    }

    public double calculatePenalty(Formulary formulary) {
        long daysOverdue = calculateDaysOverdue(formulary);
        double penaltyRatePerDay = 0.5;
        double maxPenaltyRate = 30.0;

        if (daysOverdue > 0) {
            double penalty = daysOverdue * penaltyRatePerDay;
            return Math.min(penalty, maxPenaltyRate);
        } else {
            return 0.0;
        }
    }

    public Set<Formulary> getAllFormulariesByReader(Long readerId) {
        return formularyRepository.findAllByReaderId(readerId);
    }


    public List<Book> getAllBooksFromFormularies(Set<Formulary> formularies) {
        List<Book> userBooks = new ArrayList<>();

        for (Formulary formulary : formularies) {
            // Проверяем, что формуляр не архивирован (книга находится у пользователя)
            if (!formulary.isArchived()) {
                Book book = formulary.getBook();
                userBooks.add(book);
            }
        }

        return userBooks;
    }
}



