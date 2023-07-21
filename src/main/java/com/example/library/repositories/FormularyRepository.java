package com.example.library.repositories;

import com.example.library.models.Book;
import com.example.library.models.Formulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FormularyRepository extends JpaRepository<Formulary, Long> {

    Set<Formulary> findAllByReaderId(Long readerId);
}