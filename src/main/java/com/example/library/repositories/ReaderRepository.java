package com.example.library.repositories;

import com.example.library.models.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    // Дополнительные методы для работы с базой данных
    List<Reader> findByArchivedFalse();
    Reader findByEmail(String email);
}