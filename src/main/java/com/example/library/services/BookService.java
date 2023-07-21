package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    // Создание новой книги
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }
    // Получение списка всех книг
    public List<Book> getAllBooks() {
        return bookRepository.findByArchivedFalse();
    }
    // Получение книги по её ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    // Обновление книги
    public Book updateBook(Long id, Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book existingBook = optionalBook.get();
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setYear(updatedBook.getYear());
            // Можете добавить другие свойства для обновления, если они есть в вашей модели Book.
            return bookRepository.save(existingBook);
        } else {
            // Книга с указанным ID не найдена, можете обработать эту ситуацию, если нужно.
            return null;
        }
    }
    // Архивирование книги по её ID
    public void archiveBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setArchived(true);
            bookRepository.save(book);
        } else {
            // Книга с указанным ID не найдена, можете обработать эту ситуацию, если нужно.
        }
    }
    // Подсчёт времени
}